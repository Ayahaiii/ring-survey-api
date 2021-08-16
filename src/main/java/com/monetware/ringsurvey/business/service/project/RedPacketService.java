package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.*;
import com.monetware.ringsurvey.business.pojo.dto.history.ExportHistoryDTO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectConfigsDTO;
import com.monetware.ringsurvey.business.pojo.dto.redpacket.RedPacketRecordListDTO;
import com.monetware.ringsurvey.business.pojo.dto.redpacket.RedPacketViewDTO;
import com.monetware.ringsurvey.business.pojo.dto.response.ResponseExportDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserBuyVO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BaseBuyRecord;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.history.HistoryListVO;
import com.monetware.ringsurvey.business.pojo.vo.redpacket.*;
import com.monetware.ringsurvey.business.service.user.UserService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.config.WeChatAccountConfig;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.codec.UUIDUtil;
import com.monetware.ringsurvey.system.util.file.*;
import com.monetware.ringsurvey.system.util.ip.IPUtil;
import com.monetware.ringsurvey.system.util.ip.IpInfo;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.ringsurvey.system.util.reflect.ReflectUtil;
import com.monetware.ringsurvey.system.util.wechat.WeChatPayUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStore;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class RedPacketService {

    @Autowired
    private WeChatAccountConfig weChatAccountConfig;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectResponseDao responseDao;

    @Autowired
    private AnswerService responseService;

    @Autowired
    private RedPacketConfigDao redPacketConfigDao;

    @Autowired
    private RedPacketRecordDao redPacketRecordDao;

    @Autowired
    private BuyRecordDao buyRecordDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectExportDao exportDao;

    @Value("${fileUrl.upload}")
    private String filePath;

    /**
     * 获取用户余额
     *
     * @return
     */
    public RedPacketViewDTO getUserBalance(BaseVO baseVO) {
        RedPacketViewDTO res = new RedPacketViewDTO();
        res.setBalance(userService.getUserBalance().getBalance());
        res.setStatus(projectService.getProjectConfig(baseVO.getProjectId()).getAllowRedPacket());
        return res;
    }

    /**
     * 添加订单记录
     *
     * @return
     */
    public int insertOrder(UserBuyVO userBuyVO) {
        return userService.insertOrder(userBuyVO);
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
    public PageList<Page> getBuyRecordList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        Example example = new Example(BaseBuyRecord.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", pageParam.getProjectId());
        criteria.andEqualTo("type", PayConstant.TYPE_RS);
        buyRecordDao.selectByExample(example);
        return new PageList<>(page);
    }

    /**
     * 红包列表
     *
     * @param redPacketListVO
     * @return
     */
    public PageList<Page> getRedPacketResultDTO(RedPacketRecordListVO redPacketListVO) {
        Page page = PageHelper.startPage(redPacketListVO.getPageNum(), redPacketListVO.getPageSize());
        List<RedPacketRecordListDTO> res = redPacketRecordDao.getRedPacketRecordList(redPacketListVO);
        for (RedPacketRecordListDTO dto : res) {
            dto.setTotalAmount(dto.getTotalAmount().divide(new BigDecimal(100)));
        }
        return new PageList<>(page);
    }

    /**
     * 获取项目的红包配置
     *
     * @param projectId
     * @return
     */
    public BaseRedPacketConfig getRedPacketConfig(Integer projectId) {
        Example example = new Example(BaseRedPacketConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("projectId", projectId);
        BaseRedPacketConfig config = redPacketConfigDao.selectOneByExample(example);
        if (config != null) {
            if (null == config.getMinAmount() || config.getMinAmount().compareTo(BigDecimal.ZERO) == 0) {
                config.setMinAmount(new BigDecimal(1));
            }
        }
        return config;
    }

    /**
     * 新增红包配置/红包下单
     *
     * @param redPacketConfigVO
     * @return
     */
    public int insertRedPacketConfig(RedPacketConfigVO redPacketConfigVO) {
        Integer projectId = redPacketConfigVO.getProjectId();
        BaseProject checkProject = projectDao.selectByPrimaryKey(projectId);
        if (null == checkProject) {
            throw new ServiceException(ErrorCode.PROJECT_NOT_EXIST);
        }
        ProjectConfigDTO projectConfig = projectService.getProjectConfig(projectId);
        if (projectConfig.getAllowRedPacket().equals(ProjectConstants.CLOSE)) {
            throw new ServiceException(ErrorCode.PROJECT_CONFIG_ERROR);
        }
        // 判断一些字符长度
        // send_name字段为必填并且少于32字符(来自微信开发文档)
        int sendNameLen = this.getStringLength(redPacketConfigVO.getSendName());
        if (sendNameLen >= 32) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "发放商户名称过长，限制少于32个字符");
        }
        // act_name字段必填,并且少于32个字符(来自微信开发文档)
        int actNameLen = this.getStringLength(redPacketConfigVO.getActName());
        if (actNameLen >= 32) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "活动名称过长，限制少于32个字符");
        }
        // wishing字段为必填,并且少于128个字符(来自微信开发文档)
        int wishLen = this.getStringLength(redPacketConfigVO.getWish());
        if (wishLen >= 128) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "活动名称过长，限制少于128个字符");
        }

        BaseRedPacketConfig redPacketConfig = new BaseRedPacketConfig();
        BeanUtils.copyProperties(redPacketConfigVO, redPacketConfig);

        // 计算每个固定红包金额
        BigDecimal totalAmount = redPacketConfigVO.getTotalAmount();
        BigDecimal number = new BigDecimal(redPacketConfigVO.getNumber());
        BigDecimal mixAmount = new BigDecimal(1);
        BigDecimal perAmount = mixAmount;
        if (number.compareTo(BigDecimal.ZERO) != 0) {
            perAmount = totalAmount.divide(number, 2, RoundingMode.HALF_UP);
        }
        redPacketConfig.setPerAmount(perAmount);

        // 随机金额时的最小金额
        if (redPacketConfigVO.getSendType().equals(RedPacketConstants.AMOUNT_RANDOM)) {
            if (null == redPacketConfigVO.getMinAmount() || redPacketConfigVO.getMinAmount().compareTo(BigDecimal.ZERO) == 0) {
                redPacketConfig.setMinAmount(mixAmount);
            }
        }

        if (redPacketConfig.getId() == null) {
            return redPacketConfigDao.insertSelective(redPacketConfig);
        } else {
            return redPacketConfigDao.updateByPrimaryKeySelective(redPacketConfig);
        }
    }

    /**
     * 追加项目红包金额
     *
     * @return
     */
    public int updateRedPacketConfig(RedPacketConfigVO redPacketConfigVO) {
        BaseRedPacketConfig redPacketConfig = redPacketConfigDao.selectByPrimaryKey(redPacketConfigVO.getId());
        redPacketConfig.setTotalAmount(redPacketConfig.getTotalAmount().add(redPacketConfigVO.getTotalAmount()));
        redPacketConfig.setNumber(redPacketConfig.getNumber() + redPacketConfigVO.getNumber());
        return redPacketConfigDao.updateByPrimaryKeySelective(redPacketConfig);
    }

    /**
     * 开启/关闭红包功能
     *
     * @param statusVO
     * @return
     */
    public boolean closeRedPacket(RedPacketStatusVO statusVO) {
        int projectId = statusVO.getProjectId();
        ProjectConfigsDTO configDTO = projectService.getProjectConfig(projectId);
        configDTO.setAllowRedPacket(statusVO.getStatus());
        BaseProjectConfig config = new BaseProjectConfig();
        BeanUtils.copyProperties(configDTO, config);
        BaseProject project = new BaseProject();
        project.setId(projectId);
        project.setLastModifyUser(ThreadLocalManager.getUserId());
        project.setLastModifyTime(new Date());
        project.setConfig(JSON.toJSONString(config));
        projectDao.updateByPrimaryKeySelective(project);
        if (config.getAllowRedPacket().equals(ProjectConstants.OPEN)) {
            // 红包发送记录表
            redPacketRecordDao.createCustomTable(ProjectConstants.getRedPackRecordTableName(statusVO.getProjectId()));
        }
        ProjectConfigDTO dto = new ProjectConfigDTO();
        BeanUtils.copyProperties(configDTO, dto);
        // 更新redis缓存
        return redisUtil.hset(RedisKeyConstants.projectKey(projectId), RedisKeyConstants.projectConfigKey(projectId), dto);
    }


    /**
     * 扣除项目余额
     *
     * @param redPacketConfig
     * @param amount
     * @return
     */
    public boolean subtractMoneyAccount(BaseRedPacketConfig redPacketConfig, BigDecimal amount) {
        redPacketConfig.setTotalAmount(redPacketConfig.getTotalAmount().subtract(amount));
        redPacketConfig.setNumber(redPacketConfig.getNumber() - 1);
        return redPacketConfigDao.updateByPrimaryKeySelective(redPacketConfig) > 0;
    }

    /**
     * 返还红包金额
     *
     * @param redPacketConfig
     * @param amount
     * @return
     */
    public boolean addMoneyAccount(BaseRedPacketConfig redPacketConfig, BigDecimal amount) {
        redPacketConfig.setTotalAmount(redPacketConfig.getTotalAmount().add(amount));
        redPacketConfig.setNumber(redPacketConfig.getNumber() + 1);
        return redPacketConfigDao.updateByPrimaryKeySelective(redPacketConfig) > 0;
    }

    /**
     * 审核红包
     *
     * @param recordAuditVO
     * @return
     */
    public int auditRedPacketRecord(RedPacketRecordAuditVO recordAuditVO) {
        BaseRedPacketConfig redPacketConfig = this.getRedPacketConfig(recordAuditVO.getProjectId());
        Example example = new Example(BaseRedPacketRecord.class);
        example.setTableName(ProjectConstants.getRedPackRecordTableName(recordAuditVO.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        List<Integer> ids = recordAuditVO.getIds();
        int row = 0;
        for (Integer id : ids) {
            criteria.andEqualTo("id", id);
            BaseRedPacketRecord record = redPacketRecordDao.selectOneByExample(example);
            if (null == record) {
                throw new ServiceException(ErrorCode.PACKET_RECORD_NOT_EXIST);
            }

            if (!record.getStatus().equals(RedPacketConstants.SEND_WAIT)) {
                continue;
            }

            if (recordAuditVO.getAuditResult().equals(RedPacketConstants.AUDIT_FAIL)) {
                record.setStatus(RedPacketConstants.SEND_FAIL);
                // 审核无效，返还扣除的红包金额
                boolean flag = this.addMoneyAccount(redPacketConfig, record.getTotalAmount().divide(new BigDecimal(100)));// 返还红包金额
                if (!flag) {
                    throw new ServiceException(ErrorCode.PACKET_RETURN_ERROR);
                }
            }
            record.setAuditResult(recordAuditVO.getAuditResult());
            record.setAuditUser(ThreadLocalManager.getUserId());
            record.setAuditTime(new Date());
            record.setDynamicTableName(ProjectConstants.getRedPackRecordTableName(recordAuditVO.getProjectId()));
            boolean flag = redPacketRecordDao.updateByPrimaryKeySelective(record) > 0;
            if (!flag) {
                throw new ServiceException(ErrorCode.PACKET_RECORD_AUDIT_ERROR);
            }

            row++;

            // 审核通过 发送红包
            if (recordAuditVO.getAuditResult().equals(RedPacketConstants.AUDIT_SUCCESS)) {
                BaseResponse response = responseService.getResponse(recordAuditVO.getProjectId(), null, record.getResponseGuid());
                if (StringUtils.isNotBlank(redPacketConfig.getAreaLimit())) {
                    String respIp = response.getIpAddress();
                    boolean ipAreaLimit = this.checkIpAreaLimit(redPacketConfig.getAreaLimit(), respIp);
                    if (!ipAreaLimit) {
                        // 存在红包的区域限制
                        boolean returnFlag = this.addMoneyAccount(redPacketConfig, record.getTotalAmount().divide(new BigDecimal(100)));// 返还红包金额
                        if (!returnFlag) {
                            throw new ServiceException(ErrorCode.PACKET_RETURN_ERROR);
                        }
                        throw new ServiceException(ErrorCode.PACKET_AREA_LIMIT_ERROR);
                    }
                }
                // 发送红包
                this.sendRedPacket(record, response, redPacketConfig);
            }
        }

        return row;
    }

    /**
     * 发送红包操作
     *
     * @param record
     * @param response
     * @param redPacketConfig
     */
    public void sendRedPacket(BaseRedPacketRecord record, BaseResponse response, BaseRedPacketConfig redPacketConfig) {
        // 发送红包
        Map<String, String> resultMap = this.sendRedPacketToWeiXin(record.getOpenId(), record.getMchBillNo(),
                record.getTotalAmount().intValue(), redPacketConfig);
        record.setResult(resultMap.get("return_msg"));
        if (resultMap.get("result_code").equals("SUCCESS")) {
//            this.subtractMoneyAccount(redPacketConfig, record.getTotalAmount());// 修改项目余额
            record.setStatus(RedPacketConstants.SEND_SUCCESS);// 领取状态 成功
            response.setIsSendRedpacket(RedPacketConstants.YES);// 答卷 已发送红包
        } else {
            record.setStatus(RedPacketConstants.SEND_FAIL);// 领取状态 失败
            response.setIsSendRedpacket(RedPacketConstants.NO);// 答卷 未发送红包
            // 微信发送失败，返还金额
            boolean flag = this.addMoneyAccount(redPacketConfig, record.getTotalAmount().divide(new BigDecimal(100)));
            if (!flag) {
                throw new ServiceException(ErrorCode.PACKET_RETURN_ERROR);
            }
        }
        // 回写领取记录
        this.saveRedPacketRecord(record, redPacketConfig.getProjectId());
        // 回写
        response.setDynamicTableName(ProjectConstants.getResponseTableName(redPacketConfig.getProjectId()));
        responseDao.updateByPrimaryKeySelective(response);
    }

    /**
     * 保存红包领取记录
     *
     * @param record
     * @return
     */
    public BaseRedPacketRecord saveRedPacketRecord(BaseRedPacketRecord record, Integer projectId) {
        record.setDynamicTableName(ProjectConstants.getRedPackRecordTableName(projectId));
        int row = 0;
        if (null == record.getId()) {
            row = redPacketRecordDao.insertSelective(record);
        } else {
            row = redPacketRecordDao.updateByPrimaryKeySelective(record);
        }
        if (row < 1) {
            throw new ServiceException(ErrorCode.PACKET_RECORD_SAVE_ERROR);
        }

        return record;
    }

    /**
     * 发送红包
     *
     * @param openId
     * @param mchBillNo
     * @param totalAmount
     * @param redPacketConfig
     * @return
     */
    public Map<String, String> sendRedPacketToWeiXin(String openId, String mchBillNo, int totalAmount, BaseRedPacketConfig redPacketConfig) {
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("mch_billno", mchBillNo);
        parameters.put("mch_id", weChatAccountConfig.getMchId());
        parameters.put("nonce_str", UUIDUtil.getTimestampUUID());
        parameters.put("wxappid", weChatAccountConfig.getMyAppId());
        parameters.put("send_name", redPacketConfig.getSendName());
        parameters.put("re_openid", openId);
        parameters.put("total_amount", totalAmount);
        parameters.put("total_num", 1);
        parameters.put("wishing", redPacketConfig.getWish());
        parameters.put("client_ip", "127.0.0.1");
        parameters.put("act_name", redPacketConfig.getActName());
        parameters.put("remark", "");
//        parameters.put("scene_id", "PRODUCT_1");
        parameters.put("scene_id", "PRODUCT_2");
        parameters.put("risk_info", "");
        String sign = WeChatPayUtil.createSign(parameters, weChatAccountConfig.getMchKey());
        parameters.put("sign", sign);
        String requestXML = WeChatPayUtil.getRequestXml(parameters);
        String requestUrl = RedPacketConstants.WECHAT_PAY_URL_SEND_RED_PACK;

        return this.sendToWeiXin(weChatAccountConfig.getCertPath(), requestUrl, requestXML);
    }

    /**
     * @param certPath
     * @param requestUrl
     * @param requestXML
     * @return
     */
    public Map<String, String> sendToWeiXin(String certPath, String requestUrl, String requestXML) {
        Map<String, String> result = new HashMap<>();
        try {
            CloseableHttpClient httpclient;
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream stream = new FileInputStream(new File(certPath));
            keyStore.load(stream, weChatAccountConfig.getMchId().toCharArray());
            stream.close();
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, weChatAccountConfig.getMchId().toCharArray()).build();
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"},
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            httpclient = HttpClients.custom().setSSLSocketFactory(factory).build();
            HttpPost httpPost = new HttpPost(requestUrl);
            StringEntity reqEntity = new StringEntity(requestXML, "UTF-8");
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response;
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = entity.getContent();
                SAXReader reader = new SAXReader();
                Document document = reader.read(inputStream);
                Element root = document.getRootElement();
                List<Element> elementList = root.elements();
                for (Element e : elementList) {
                    result.put(e.getName(), e.getText());
                }
                inputStream.close();
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int getStringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        if (StringUtils.isNotBlank(value)) {
            for (int i = 0; i < value.length(); i++) {
                /* 获取一个字符 */
                String temp = value.substring(i, i + 1);
                /* 判断是否为中文字符 */
                if (temp.matches(chinese)) {
                    /* 中文字符长度为2 */
                    valueLength += 2;
                } else {
                    /* 其他字符长度为1 */
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    /**
     * 导出红包记录
     *
     * @param listVO
     * @return
     */
    public int exportRedRacketRecord(RedPacketRecordListVO listVO) throws Exception {
        Integer projectId = listVO.getProjectId();
        //
        List<RedPacketRecordListDTO> data = redPacketRecordDao.getRedPacketRecordList(listVO);
        JSONArray array = JSON.parseArray(JSON.toJSONString(data));
        // 文件路径
        String pre = "/export/" + listVO.getProjectId() + "/redpacket/";
        String path = filePath + pre;

        String[] propertyArr = ReflectUtil.getFieldName(new RedPacketRecordListDTO());
        List<String> properties = Arrays.asList(propertyArr);
        Map<String, Object> res = ExcelUtil.createExcelFile("REDPACKET", properties, array, path);

        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(res.get("fileName").toString());
        exportHistory.setFileSize(Long.parseLong(res.get("fileSize").toString()));
        exportHistory.setFilePath(pre + res.get("fileName").toString());
        exportHistory.setType("REDPACKET");
        exportHistory.setFileType("EXCEL");
        exportHistory.setProjectId(projectId);
        exportHistory.setDescription("红包领取记录");
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(new Date());
        int row = exportDao.insert(exportHistory);
        if (row > 0) {
            // 回写信息量总数
            BaseProject p = new BaseProject();
            p.setFileSize(Long.parseLong(res.get("fileSize").toString()));
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(new Date());
            p.setId(projectId);
            projectDao.updateProjectAdd(p);
        }
        return row;
    }

    /**
     * 红包记录导出列表
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getRedPacketDownList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        HistoryListVO listVO = new HistoryListVO();
        listVO.setProjectId(pageParam.getProjectId());
        listVO.setType("REDPACKET");
        List<ExportHistoryDTO> historyList = exportDao.getExportHistory(listVO);
        for (ExportHistoryDTO historyDTO : historyList) {
            historyDTO.setFileSizeStr(FileUtil.byteFormat(historyDTO.getFileSize(), true));
        }
        return new PageList<>(page);
    }

    /**
     * 删除导出记录文件
     *
     * @param delVO
     * @return
     */
    public int deleteDownFile(RedPacketDeleteVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/redpacket/";
        // 回写信息量总数
        BaseProject p = new BaseProject();
        p.setFileSize(new File(path + exportHistory.getName()).length());
        p.setLastModifyUser(ThreadLocalManager.getUserId());
        p.setLastModifyTime(new Date());
        p.setId(delVO.getProjectId());
        projectDao.updateProjectDel(p);
        boolean flag = FileUtil.deleteFile(path, exportHistory.getName());
        if (flag) {
            return exportDao.deleteByPrimaryKey(delVO.getId());
        } else {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "文件删除失败");
        }
    }

    /**
     * 下载红包领取记录文件
     *
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadRedPacketRecord(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/redpacket/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * @param projectId
     * @param id
     * @param responseGuid
     * @return
     */
    public BaseRedPacketRecord getRedPacketRecord(Integer projectId, Integer id, String responseGuid) {
        Example example = new Example(BaseRedPacketRecord.class);
        example.setTableName(ProjectConstants.getRedPackRecordTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        criteria.orEqualTo("responseGuid", responseGuid);
        return redPacketRecordDao.selectOneByExample(example);
    }

    /**
     * 设置地区限制
     *
     * @param areaLimitVO
     * @return
     */
    public int setAreaLimit(AreaLimitVO areaLimitVO) {
        BaseRedPacketConfig redPacketConfig = this.getRedPacketConfig(areaLimitVO.getProjectId());
        int row = 0;
        if (StringUtils.isNotBlank(areaLimitVO.getAreaLimit())) {
            try {
                JSONArray jsonObject = JSONArray.parseArray(areaLimitVO.getAreaLimit());
                redPacketConfig.setAreaLimit(jsonObject.toJSONString());
                row = redPacketConfigDao.updateByPrimaryKeySelective(redPacketConfig);
            } catch (Exception e) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, e.getMessage());
            }
        }
        return row;
    }

    /**
     * check地区限制
     *
     * @param areaLimit
     * @param ip
     * @return
     */
    public boolean checkIpAreaLimit(String areaLimit, String ip) {
        boolean res = false;
        if (StringUtils.isBlank(areaLimit) || StringUtils.isBlank(ip)) {
            return true;
        }

        IpInfo ipInfo = IPUtil.ip2Region(ip);
        JSONArray areaArray = JSONArray.parseArray(areaLimit);
        for (int i = 0; i < areaArray.size(); i++) {
            JSONObject areaObj = areaArray.getJSONObject(i);
            String province = areaObj.getString("province");
            JSONArray cityArray = areaObj.getJSONArray("city");
            if (null == cityArray || cityArray.isEmpty()) {
                if (province.equals(ipInfo.getPro())) {
                    res = true;
                }
            } else {
                if (cityArray.contains(ipInfo.getCity())) {
                    res = true;
                }
            }
        }
        return res;
    }

}
