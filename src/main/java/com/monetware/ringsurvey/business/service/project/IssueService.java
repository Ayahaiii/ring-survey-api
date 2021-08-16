package com.monetware.ringsurvey.business.service.project;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.*;
import com.monetware.ringsurvey.business.pojo.dto.issue.MultiplySourceIssueDTO;
import com.monetware.ringsurvey.business.pojo.dto.issue.PublishLogDTO;
import com.monetware.ringsurvey.business.pojo.dto.issue.SmsEmailViewDTO;
import com.monetware.ringsurvey.business.pojo.dto.issue.WxShareDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserBuyVO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BaseBuyRecord;
import com.monetware.ringsurvey.business.pojo.po.publishConfig.EmailAccount;
import com.monetware.ringsurvey.business.pojo.po.publishConfig.MessageAccount;
import com.monetware.ringsurvey.business.pojo.vo.issue.*;
import com.monetware.ringsurvey.business.service.user.UserService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.config.EmailConfig;
import com.monetware.ringsurvey.system.config.MessageConfig;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.codec.MD5;
import com.monetware.ringsurvey.system.util.date.DateUtil;
import com.monetware.ringsurvey.system.util.file.CommonProperty;
import com.monetware.ringsurvey.system.util.http.HttpRequestUtil;
import com.monetware.ringsurvey.system.util.list.ListUtil;
import com.monetware.ringsurvey.system.util.qiniu.QiNiuUtils;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class IssueService {

    @Autowired
    private MessageConfig messageConfig;

    @Autowired
    private EmailConfig emailConfig;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectPublishConfigDao publishConfigDao;

    @Autowired
    private ProjectPublishLogDao publishLogDao;

    @Autowired
    private ProjectPublishTaskDao publishTaskDao;

    @Autowired
    private ProjectPublishQueueDao publishQueueDao;

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private QiNiuUtils qiNiuUtils;

    @Value("${qiniu.url}")
    private String qiNiuUrl;

    @Value("${fileUrl.upload}")
    private String filePath;

    @Autowired
    private UserService userService;

    @Autowired
    private BuyRecordDao buyRecordDao;

    @Autowired
    private MultiplySourceIssueDao multiplySourceIssueDao;

    public String linkIssue(Integer projectId) {
        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        return project.getPublishCode();
    }

    /**
     * 上传分享图片
     *
     * @param file
     * @return
     */
    public String uploadWxShareImg(MultipartFile file) {
        return qiNiuUrl + qiNiuUtils.uploadFile(file, 1);
    }

    /**
     * 删除分享图片
     *
     * @param imgDelVO
     * @return
     */
    public void deleteWxShareImg(WxShareImgDelVO imgDelVO) {
        String fileName = imgDelVO.getImgUrl().substring(imgDelVO.getImgUrl().lastIndexOf("/") + 1);
        redisUtil.hdel(RedisKeyConstants.wxShareKey(imgDelVO.getCode()), "imgUrl");
        qiNiuUtils.deleteFile(fileName, 1);
    }

    /**
     * 保存微信分享
     *
     * @param shareVO
     */
    public void saveWxShare(WxShareVO shareVO) {
        String key = RedisKeyConstants.wxShareKey(shareVO.getCode());
        redisUtil.hset(key, "title", shareVO.getTitle());
        redisUtil.hset(key, "desc", shareVO.getDesc());
        redisUtil.hset(key, "imgUrl", shareVO.getImgUrl());
    }

    /**
     * 获取微信分享
     *
     * @param imgDelVO
     */
    public WxShareDTO getWxShare(WxShareImgDelVO imgDelVO) {
        WxShareDTO res = new WxShareDTO();
        String key = RedisKeyConstants.wxShareKey(imgDelVO.getCode());
        if (redisUtil.hasKey(key)) {
            res.setTitle(redisUtil.hget(key, "title").toString());
            res.setInfo(redisUtil.hget(key, "desc").toString());
            res.setImgUrl(redisUtil.hget(key, "imgUrl").toString());
        }
        return res;
    }

    /**
     * 配置保存
     *
     * @param publishConfigVO
     * @return
     */
    public BaseProjectPublishConfig savePublishConfig(PublishConfigVO publishConfigVO) {
        ProjectConfigDTO configDTO = projectService.getProjectConfig(publishConfigVO.getProjectId());
        if (ProjectConstants.CLOSE.equals(configDTO.getAllowSmsAEmail())) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目配置参数错误，未启用短信、邮件配置");
        }

        Date now = new Date();
        BaseProjectPublishConfig publishConfig;
        int row = 0;
        // 金额
        BigDecimal buyAmount = new BigDecimal(publishConfigVO.getNum()).multiply(new BigDecimal(0.1));
        if (null == publishConfigVO.getId()) {
            publishConfig = new BaseProjectPublishConfig();
            BeanUtils.copyProperties(publishConfigVO, publishConfig);
            publishConfig.setTotalAmount(buyAmount);
            publishConfig.setCreateTime(now);
            row = publishConfigDao.insertSelective(publishConfig);
        } else {
            publishConfig = publishConfigDao.selectByPrimaryKey(publishConfigVO.getId());
            BeanUtils.copyProperties(publishConfigVO, publishConfig);
            publishConfig.setTotalAmount(buyAmount);
            publishConfig.setLastModifyTime(now);
            row = publishConfigDao.updateByPrimaryKeySelective(publishConfig);
        }
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目发布配置保存失败");
        }
        return publishConfig;
    }

    /**
     * 获取配置
     *
     * @param projectId
     * @param type
     * @return
     */
    public BaseProjectPublishConfig getPublishConfig(Integer projectId, Integer type) {
        BaseProjectPublishConfig publishConfig = new BaseProjectPublishConfig();
        publishConfig.setProjectId(projectId);
        publishConfig.setType(type);
        return publishConfigDao.selectOne(publishConfig);
    }

    /**
     * 任务保存
     *
     * @param taskVO
     * @return
     */
    public int savePublishTask(PublishTaskVO taskVO) {
        // TODO 定时任务
        return 0;
    }

    /**
     * 任务删除
     *
     * @param taskId
     * @return
     */
    public int deletePublishTask(Integer taskId) {
        BaseProjectPublishTask publishTask = new BaseProjectPublishTask();
        publishTask.setId(taskId);
        int row = publishTaskDao.deleteByPrimaryKey(publishTask);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "发送任务删除失败");
        }
        return row;
    }

    /**
     * 队列删除
     *
     * @return
     */
    public int deletePublishQueue(Integer queueId, Integer projectId) {
        BaseProjectPublishQueue queue = new BaseProjectPublishQueue();
        queue.setDynamicTableName(ProjectConstants.getPublishQueueTableName(projectId));
        queue.setId(queueId);
        int row = publishQueueDao.delete(queue);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "发送队列删除失败");
        }
        return row;
    }

    /**
     * 发布日志列表
     *
     * @param searchVO
     * @return
     */
    public PageList<Page> getPublishLogs(PublishSearchVO searchVO) {
        Date now = new Date();
        Page page = PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<PublishLogDTO> res = publishLogDao.getPublishLogs(searchVO);
        for (PublishLogDTO logDTO : res) {
            long ct = DateUtil.getDateDuration(logDTO.getCreateTime(), now);
            if (ct < 7 * 24 * 60 * 60L) {
                logDTO.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
            }
        }
        return new PageList<>(page);
    }

    /**
     * 日志保存
     *
     * @param logVO
     * @return
     */
    public int savePublishLog(PublishLogVO logVO, Integer projectId) {
        BaseProjectPublishLog publishLog = new BaseProjectPublishLog();
        publishLog.setDynamicTableName(ProjectConstants.getPublishLogTableName(projectId));
        BeanUtils.copyProperties(logVO, publishLog);
        publishLog.setCreateTime(new Date());
//        publishLog.setCreateUser(ThreadLocalManager.getUserId());
        int row = publishLogDao.insertSelective(publishLog);
        if (row < 1) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "发布日志保存失败");
        }
        return row;
    }

    /**
     * 短信发送
     *
     * @param publishTo
     * @param subject
     * @param content
     * @return
     */
    public boolean sendMessage(MessageAccount messageAccount, String publishTo, String subject, String content) {
        boolean result = false;
//        List<BaseProjectPublishConfig> configList = this.getPublishConfigList(IssueConstants.PUBLISH_TYPE_MESSAGE);
//        String config = configList.get(0).getConfig();
//        JSONObject jsonObject = JSONObject.parseObject(config);
//
//        String url = jsonObject.getString("url");
//        String username = jsonObject.getString("username");
//        String password = jsonObject.getString("password");
//        String productId = jsonObject.getString("productId");
//        String sign = jsonObject.getString("sign");
//        String mobile = publishTo;

        String url = messageAccount.getUrl();
        String username = messageAccount.getUsername();
        String password = messageAccount.getPassword();
        String productId = messageAccount.getProductId();
        String sign = messageAccount.getSign();
        String mobile = publishTo;

        content += " 退订回T";
        String xh = "";
        String tkey = DateUtil.DateToString(new Date(), "yyyyMMddHHmmss");
        try {
            content = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "url=" + url
                + "&username=" + username
                + "&password=" + MD5.getMD5ofStr(MD5.getMD5ofStr(password) + tkey)
                + "&tkey=" + tkey
                + "&mobile=" + mobile
                + "&content=" + content
                + "&productid=" + productId
                + "&xh" + xh;
        String ret = HttpRequestUtil.sendPost(url, param);//sendPost or sendGet  即get和post方式
        String retArray[] = ret.split(",");
        if (retArray[0].equals("1")) {
            result = true;
        } else {
            // System.out.println("ret:"+ret);
            result = false;
        }

        return result;
    }

    /**
     * 邮件发送
     *
     * @param publishTo
     * @param subject
     * @param content
     * @return
     */
    public boolean sendEmail(EmailAccount emailAccount, String publishTo, String subject, String content) {
        boolean result = false;
//        List<BaseProjectPublishConfig> configList = this.getPublishConfigList(IssueConstants.PUBLISH_TYPE_EMAIL);
//        BaseProjectPublishConfig emailConfig = configList.get(0);

        Email email = new SimpleEmail();
        // smtp host
        email.setHostName(emailAccount.getServer());
        // SSL验证
        email.setSSLOnConnect(true);
        email.setSSLCheckServerIdentity(true);
        email.setSmtpPort(465);
        email.setSslSmtpPort("465");
        // 登陆邮件服务器的用户名和密码
        email.setAuthentication(emailAccount.getUsername(), emailAccount.getPassword());
        email.setCharset("UTF-8");
        try {
            email.setFrom(emailAccount.getUsername());
            email.addTo(publishTo);
            email.setSubject(subject);
            email.setMsg(content);
            email.send();
            result = true;
        } catch (EmailException e) {
            e.printStackTrace();
//            System.out.println(("要发送给" + publishTo + "的邮件发送失败，错误代码：" + e.getMessage()));
            result = false;
        }

        return result;
    }

    /**
     * 获取可用配置列表
     *
     * @param type
     * @return
     */
    private List<BaseProjectPublishConfig> getPublishConfigList(Integer type) {
        Example example = new Example(BaseProjectPublishConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", type);
        criteria.andEqualTo("status", ProjectConstants.OPEN);
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        criteria.andEqualTo("testSuccess", 1);// 测试发送成功的
        List<BaseProjectPublishConfig> configList = publishConfigDao.selectByExample(example);
        if (null == configList || configList.size() == 0) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "无任何可用配置");
        }
        return configList;
    }

    /**
     * 测试发送
     *
     * @param testSendVO
     * @return
     */
    public boolean sendTestExample(TestSendVO testSendVO) {
        boolean result = false;
        String subject = "测试案例(Test Example)";
        String content = "测试发送(Test Send)";
        if (testSendVO.getType().equals(IssueConstants.PUBLISH_TYPE_MESSAGE)) {
            // 发送测试短信
            MessageAccount messageAccount = this.messageConfig.getList().get(0);
            result = this.sendMessage(messageAccount, testSendVO.getPublishTo(), subject, content);
        } else {
            // 发送测试邮件
            EmailAccount emailAccount = this.emailConfig.getList().get(0);
            result = this.sendEmail(emailAccount, testSendVO.getPublishTo(), subject, content);
        }
        return result;
    }

    /**
     * 短信、邮件发布
     *
     * @param publishSendVO
     */
    public void doPublish(PublishSendVO publishSendVO) {
        /**
         * 20200421修改逻辑
         * 使用锐研账号进行发送（账号在配置文件中）后续可以追加下单功能，进行收费
         * 短信、邮件发布那边保存的是发送内容模板
         * 发送按钮放到样本模块（选择样本、全部样本）
         */
        Integer projectId = publishSendVO.getProjectId();

        Integer sendCount = publishSendVO.getSendCountPer();
        if (null == sendCount) {
            sendCount = 0;
        }

        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        ProjectConfigDTO projectConfig = projectService.getProjectConfig(projectId);
        if (projectConfig.getAllowSmsAEmail().equals(ProjectConstants.CLOSE)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "项目未开启短信、邮件配置");
        }

        // 发送配置
        BaseProjectPublishConfig publishConfig = this.getPublishConfig(projectId, publishSendVO.getType());
        if (null == publishConfig || StringUtils.isBlank(publishConfig.getContent())) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "请前往配置对应的发布内容");
        }

        // 样本列表
        List<HashMap<String, Object>> sampleMapList = new ArrayList<>();
        Integer[] statusArray = {SampleConstants.STATUS_INIT, SampleConstants.STATUS_ASSIGN};
        if (publishSendVO.getMethod().equals(IssueConstants.PUBLISH_METHOD_ALL)) {// 全部样本
            sampleMapList = sampleDao.getSampleMapListByStatus(projectId, Arrays.asList(statusArray));
        } else {
            sampleMapList = sampleDao.getSampleMapListByIds(projectId, Arrays.asList(statusArray), publishSendVO.getSampleIds());
        }

        // 整理内容
        List<BaseProjectPublishQueue> queueList = new ArrayList<>();
        for (HashMap<String, Object> sample : sampleMapList) {
            BaseProjectPublishQueue publishQueue = new BaseProjectPublishQueue();
            String sampleGuid = sample.get("sample_guid").toString();
            publishQueue.setSampleGuid(sampleGuid);
            String code = sample.get("code").toString();

            String publishToEmail = "";
            String publishToPhone = "";
            if (publishSendVO.getType().equals(IssueConstants.PUBLISH_TYPE_MESSAGE)) {
                if (null == sample.get("phone") || StringUtils.isBlank(sample.get("phone").toString())) {
                    if (null == sample.get("mobile") || StringUtils.isBlank(sample.get("mobile").toString())) {
                        continue;
                    } else {
                        publishToPhone = sample.get("mobile").toString();
                    }
                } else {
                    publishToPhone = sample.get("phone").toString();
                }
                // 长度11位
                if (! publishToPhone.matches("^[1]\\d{10}$")) {
                    continue;
                }
                publishQueue.setPublishTo(publishToPhone);
                publishQueue.setType(IssueConstants.PUBLISH_TYPE_MESSAGE);
            } else {
                if (null == sample.get("email") || StringUtils.isBlank(sample.get("email").toString())) {
                    continue;
                }
                publishToEmail = sample.get("email").toString();
                publishQueue.setPublishTo(publishToEmail);
                publishQueue.setType(IssueConstants.PUBLISH_TYPE_EMAIL);
            }

            // 发布链接
            String url = publishSendVO.getAnswerUrl() + "/" + project.getPublishCode()  + code;
            String content = publishConfig.getContent().replaceAll("%url%", url);
            // 替换样本属性
            for (String propertyCode : sample.keySet()) {
                content = StringUtils.replace(content, "%" + CommonProperty.sampleDBMap.get(propertyCode) + "%",
                        sample.get(propertyCode).toString());
            }
            // 问卷密码
            String password = projectConfig.getResponsePasswordValue();
            content = StringUtils.replace(content, "%password%", password);

            publishQueue.setContent(content);
            publishQueue.setSubject(publishConfig.getSubject());
            publishQueue.setCreateUser(ThreadLocalManager.getUserId());
            publishQueue.setCreateTime(new Date());
            queueList.add(publishQueue);
        }

        if (publishConfig.getNum() < queueList.size()) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "可用发布条数不足，请前往充值");
        }

        // 是否立即发送,定时任务存队列,立即发送不存队列
        if (publishSendVO.getIsTask().equals(ProjectConstants.OPEN)) {
            // 发送队列
            queueList.get(0).setDynamicTableName(ProjectConstants.getPublishQueueTableName(project.getId()));
            int count = publishQueueDao.insertList(queueList);
        } else {
            // 处理发送数量
            List<List<BaseProjectPublishQueue>> sendList = ListUtil.averageAssign(queueList, 500);

            // 立即发送
            int poolSize = Runtime.getRuntime().availableProcessors() + 1;// 处理密集型
            ThreadPoolExecutor newFixedThreadPool = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(256), // 使用有界队列，避免OOM
                    new ThreadPoolExecutor.DiscardPolicy());
            for (int i = 0; i < sendList.size(); i++) {
                List<BaseProjectPublishQueue> queues = sendList.get(i);
                PublishThread thread = new PublishThread(projectId, queues, messageConfig.getList(), emailConfig.getList());
                thread.setIsTask(ProjectConstants.CLOSE);
                thread.setSendInterval(publishSendVO.getSendInterval());// 发送间隔
                newFixedThreadPool.execute(thread);
            }
            newFixedThreadPool.shutdown();
            while (true) {
                if (newFixedThreadPool.isTerminated()) {
                    log.info("所有的发送子线程都结束了！");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 回写样本发送次数
     *
     * @param projectId
     * @param sampleGuid
     * @param sendType
     */
    public int updateSample(Integer projectId, String sampleGuid, Integer sendType) {
        Example example = new Example(BaseProjectSample.class);
        example.setTableName(ProjectConstants.getSampleTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sampleGuid", sampleGuid);
        BaseProjectSample sample = sampleDao.selectOneByExample(example);
        sample.setDynamicTableName(ProjectConstants.getSampleTableName(projectId));

        Integer smsTimes = sample.getSmsTimes();
        if (null == smsTimes) {
            smsTimes = 0;
        }
        Integer emailTimes = sample.getEmailTimes();
        if (null == emailTimes) {
            emailTimes = 0;
        }

        if (sendType.equals(IssueConstants.PUBLISH_TYPE_MESSAGE)) {
            smsTimes++;
        }
        if (sendType.equals(IssueConstants.PUBLISH_TYPE_EMAIL)) {
            emailTimes++;
        }
        sample.setSmsTimes(smsTimes);
        sample.setEmailTimes(emailTimes);
        return sampleDao.updateByPrimaryKeySelective(sample);
    }

    /**
     * 获取用户余额、剩余发送条数
     *
     * @return
     */
    public SmsEmailViewDTO getUserBalance(PublishInitVO initVO) {
        Integer projectId = initVO.getProjectId();
        Example example = new Example(BaseProjectPublishConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        criteria.andEqualTo("type", initVO.getType());
        BaseProjectPublishConfig publishConfig = publishConfigDao.selectOneByExample(example);

        SmsEmailViewDTO res = new SmsEmailViewDTO();
        res.setType(initVO.getType());
        if (null != publishConfig) {
            if (null == publishConfig.getNum() || publishConfig.getNum() == 0) {
                res.setNum(0);
            } else {
                res.setNum(publishConfig.getNum());
            }
            res.setStatus(ProjectConstants.OPEN);
        } else {
            res.setNum(0);
            res.setStatus(ProjectConstants.CLOSE);
        }
        res.setBalance(userService.getUserBalance().getBalance());
        return res;
    }

    /**
     * 添加订单记录
     *
     * @param userBuyVO
     * @return
     */
    public int insertOrder(UserBuyVO userBuyVO) {
        userBuyVO.setOrderAmount(new BigDecimal(userBuyVO.getNum()).multiply(new BigDecimal(0.1)));
        return userService.insertIssueOrder(userBuyVO);
    }

    /**
     * 支付订单
     */
    public int insertBuyOrder(Integer id) {
        return userService.insertBuyOrder(id);
    }

    /**
     * 获取支付订单记录
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getPayOrderList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        Example example = new Example(BaseBuyRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", pageParam.getProjectId());
        criteria.andEqualTo("type", PayConstant.TYPE_RS);
        buyRecordDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 追加条数
     *
     * @param configVO
     * @return
     */
    public int addSmsAndEmailNum(PublishConfigVO configVO) {
        BaseProjectPublishConfig config = this.getPublishConfig(configVO.getProjectId(), configVO.getType());
        // 0.1元/条
        BigDecimal orderAmount = new BigDecimal(0.1 * configVO.getNum());
        configVO.setAmount(orderAmount);
        config.setTotalAmount(config.getTotalAmount().add(configVO.getAmount()));
        config.setNum(config.getNum() + configVO.getNum());
        return publishConfigDao.updateByPrimaryKeySelective(config);
    }

    /**
     * 扣减余额和可用条数
     *
     * @param projectId
     * @param type
     * @return
     */
    public synchronized int subtractBalanceAndNum(Integer projectId, Integer type) {
        BaseProjectPublishConfig publishConfig = this.getPublishConfig(projectId, type);
        publishConfig.setTotalAmount(publishConfig.getTotalAmount().subtract(new BigDecimal(0.1)));
        publishConfig.setNum(publishConfig.getNum() - 1);
        return publishConfigDao.updateByPrimaryKeySelective(publishConfig);
    }

    /**
     * 发送线程
     */
    private class PublishThread implements Runnable {

        private Integer projectId;
        private List<BaseProjectPublishQueue> taskQueue;
        private List<MessageAccount> messageAccounts;
        private List<EmailAccount> emailAccounts;
        private Integer isTask;// 0-立即发送 1-定时任务
        private Integer sendInterval;
        private int topLimit;// 单个账号的发送上限

        public PublishThread(Integer projectId, List<BaseProjectPublishQueue> taskQueue, List<MessageAccount> messageAccounts,
                             List<EmailAccount> emailAccounts) {
            this.projectId = projectId;
            this.taskQueue = taskQueue;
            this.messageAccounts = messageAccounts;
            this.emailAccounts = emailAccounts;
        }

        public void setSendInterval(Integer sendInterval) {
            this.sendInterval = sendInterval;
        }

        public void setIsTask(Integer isTask) {
            this.isTask = isTask;
        }

        @Override
        public void run() {
            int index = -1;
            for (int i = 0; i < taskQueue.size(); i++) {
                BaseProjectPublishQueue queue = taskQueue.get(i);
                Integer type = queue.getType();
                boolean result = false;
                if (IssueConstants.PUBLISH_TYPE_MESSAGE.equals(type)) {
                    result = sendMessage(messageAccounts.get(0), queue.getPublishTo(), queue.getSubject(), queue.getContent());
                } else {
                    result = sendEmail(emailAccounts.get(0), queue.getPublishTo(), queue.getSubject(), queue.getContent());
                }

                // 发布日志
                PublishLogVO publishLogVO = new PublishLogVO();
                publishLogVO.setType(type);
                publishLogVO.setSampleGuid(queue.getSampleGuid());
                publishLogVO.setSubject(queue.getSubject());
                publishLogVO.setContent(queue.getContent());
                publishLogVO.setPublishTo(queue.getPublishTo());
                if (result) {
                    publishLogVO.setStatus(IssueConstants.SEND_SUCCESS);
                    // 扣减余额
                    subtractBalanceAndNum(projectId, type);
                } else {
                    publishLogVO.setStatus(IssueConstants.SEND_FAIL);
                }
                savePublishLog(publishLogVO, projectId);

                // 回写发送次数
                updateSample(projectId, queue.getSampleGuid(), type);

                // 若定时任务，则删除任务队列
                if (isTask == ProjectConstants.OPEN) {
                    deletePublishQueue(queue.getId(), projectId);
                }

                // 发送间隔
                if (null != sendInterval) {
                    try {
                        Thread.sleep(1000 * sendInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 新增多来源链接
     *
     * @return
     */
    public List<MultiplySourceIssueDTO> insertMultiplySourceIssue(MultiplySourceIssueVO sourceIssueVO) {
        Date now = new Date();
        BaseMultiplySourceIssue multiplySourceIssue = new BaseMultiplySourceIssue();
        BeanUtils.copyProperties(sourceIssueVO, multiplySourceIssue);
        multiplySourceIssue.setIsDelete(ProjectConstants.DELETE_NO);
        multiplySourceIssue.setCreateUser(ThreadLocalManager.getUserId());
        multiplySourceIssue.setCreateTime(now);
        multiplySourceIssue.setLastModifyUser(ThreadLocalManager.getUserId());
        multiplySourceIssue.setLastModifyTime(now);
        multiplySourceIssueDao.insertSelective(multiplySourceIssue);
        return this.getMultiplySourceIssues(sourceIssueVO.getProjectId());
    }

    /**
     * 获取项目的多来源链接
     *
     * @param projectId
     * @return
     */
    public List<MultiplySourceIssueDTO> getMultiplySourceIssues(Integer projectId) {
        return multiplySourceIssueDao.getMultiplySourceIssues(projectId);
    }

    /**
     * 删除多来源链接
     *
     * @param sourceId
     * @return
     */
    public List<MultiplySourceIssueDTO> deleteMultiplySourceIssue(Integer sourceId) {
        BaseMultiplySourceIssue multiplySourceIssue = multiplySourceIssueDao.selectByPrimaryKey(sourceId);
        // 物理删除
        multiplySourceIssueDao.deleteByPrimaryKey(sourceId);
        return multiplySourceIssueDao.getMultiplySourceIssues(multiplySourceIssue.getProjectId());
    }

}
