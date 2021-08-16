package com.monetware.ringsurvey.business.service.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.JsonArray;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.*;
import com.monetware.ringsurvey.business.pojo.dto.project.ConfigDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.*;
import com.monetware.ringsurvey.business.pojo.po.BaseProject;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectPublishConfig;
import com.monetware.ringsurvey.business.pojo.po.BaseRedPacketConfig;
import com.monetware.ringsurvey.business.pojo.po.BaseUser;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BaseBuyRecord;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BasePayOrder;
import com.monetware.ringsurvey.business.pojo.vo.user.*;
import com.monetware.ringsurvey.business.service.auth.AuthService;
import com.monetware.ringsurvey.business.service.project.IssueService;
import com.monetware.ringsurvey.business.service.project.ProjectService;
import com.monetware.ringsurvey.business.service.project.RedPacketService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.pay.CPayUtil;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private BuyRecordDao buyRecordDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectPublishConfigDao publishConfigDao;

    @Autowired
    private RedPacketService redPacketService;

    @Autowired
    private RedPacketConfigDao redPacketConfigDao;

    @Value("${outurl.authCreateOrderUrl}")
    private String authCreateOrderUrl;

    @Value("${outurl.authGetBalanceUrl}")
    private String authGetBalanceUrl;

    @Value("${outurl.authPayOrderUrl}")
    private String authPayOrderUrl;

    /**
     * 返回用户前台权限
     *
     * @return
     */
    public UserPermissionDTO getUserPermission() {
        Date now = new Date();
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        if (user == null) {
            user = new BaseUser();
            user.setId(ThreadLocalManager.getUserId());
            user.setName(ThreadLocalManager.getTokenContext().getName());
            user.setRole(UserConstants.ROLE_COMMON);
            user.setStatus(UserConstants.STATUS_ENABLE);
            user.setCreateTime(now);
            userDao.insertSelective(user);
        }
        user.setName(ThreadLocalManager.getTokenContext().getName());
        user.setTelephone(ThreadLocalManager.getTokenContext().getTelPhone());
        user.setEmail(ThreadLocalManager.getTokenContext().getEmail());
        user.setLastLoginTime(now);
        // 首次登录更新用户信息
        userDao.updateByPrimaryKeySelective(user);
        UserPermissionDTO userPermissionDTO = new UserPermissionDTO();
        userPermissionDTO.setRole(user.getRole());
        return userPermissionDTO;
    }

    /**
     * 返回用户前台权限（落地部署用）
     *
     * @return
     */
    public UserPermissionDTO getUserPermissionLocal() {
        Date now = new Date();
        ConfigDTO configDTO = projectService.getConfig();
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        if (user == null) {
            user = new BaseUser();
            user.setId(ThreadLocalManager.getUserId());
            user.setName(ThreadLocalManager.getTokenContext().getName());
            user.setRole(UserConstants.ROLE_COMMON);
            user.setStatus(UserConstants.STATUS_ENABLE);
            user.setProjectAuth(this.authList2JSONString(configDTO.getProjectAuth()));
            user.setCreateTime(now);
            userDao.insertSelective(user);
        }
        user.setName(ThreadLocalManager.getTokenContext().getName());
        user.setTelephone(ThreadLocalManager.getTokenContext().getTelPhone());
        user.setEmail(ThreadLocalManager.getTokenContext().getEmail());
        user.setLastLoginTime(now);
        // 首次登录更新用户信息
        userDao.updateByPrimaryKeySelective(user);
        UserPermissionDTO userPermissionDTO = new UserPermissionDTO();
        userPermissionDTO.setRole(user.getRole());
        userPermissionDTO.setProjectAuth(this.getAuthList(user.getProjectAuth()));
        return userPermissionDTO;
    }

    /**
     * 查询用户列表
     *
     * @param userSearchVO
     * @return
     */
    public UserSearchDTO searchUser(UserSearchVO userSearchVO) {
        return userDao.searchUser(userSearchVO);
    }

    /**
     * 获取用户余额
     *
     * @return
     */
    public UserBalanceDTO getUserBalance() {
        Map<String, Object> params = new HashMap<>();
        UserBalanceDTO userBalanceDTO = new UserBalanceDTO();
        Map<String, Object> res = (Map<String, Object>) authService.getResponseResult(params, authGetBalanceUrl);
        userBalanceDTO.setBalance(new BigDecimal(Double.parseDouble(res.get("balance").toString())));
        userBalanceDTO.setUseBalance(new BigDecimal(Double.parseDouble(res.get("useBalance").toString())));
        return userBalanceDTO;
    }

    /**
     * 修改用户信息（落地部属用）
     *
     * @param updateVO
     * @return
     */
    public UserInfoDTO updateUserInfo(UserUpdateVO updateVO) {
        UserInfoDTO res = new UserInfoDTO();
        BaseUser baseUser = userDao.selectByPrimaryKey(updateVO.getId());
        if (!checkPhoneAvailable(updateVO.getTelephone(), baseUser.getTelephone())) {
            throw new ServiceException(ErrorCode.PHONE_HAVE_BEEN_USED);
        }
        baseUser.setTelephone(updateVO.getTelephone());
        baseUser.setEmail(updateVO.getEmail());
        baseUser.setGender(updateVO.getGender());
        baseUser.setCollege(updateVO.getCollege());
        baseUser.setEducation(updateVO.getEducation());
        baseUser.setCompany(updateVO.getCompany());
        //拼接生日
        if (StringUtils.isBlank(updateVO.getYear())) {
            baseUser.setBirthday("1949-10");
        } else {
            baseUser.setBirthday(updateVO.getYear() + "-" + updateVO.getMonth());
        }
        userDao.updateByPrimaryKey(baseUser);
        BeanUtils.copyProperties(baseUser, res);
        res.setYear(updateVO.getYear());
        res.setMonth(updateVO.getMonth());
        res.setAuthList(this.getAuthList(baseUser.getProjectAuth()));
        return res;
    }

    private List<String> getAuthList(String projectAuth) {
        List<String> authList = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(projectAuth);
        if (null != jsonArray && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                authList.add(jsonArray.getString(i));
            }
        }
        return authList;
    }

    private String authList2JSONString(List<String> authList) {
        if (null == authList || authList.isEmpty()) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < authList.size(); i++) {
            jsonArray.add(authList.get(i));
        }
        return jsonArray.toJSONString();
    }

    /**
     * 修改用户创建项目的权限（落地部署用）
     *
     * @param userAuthVO
     * @return
     */
    public int updateUserAuth(UserAuthVO userAuthVO) {
        ConfigDTO configDTO = projectService.getConfig();
        BaseUser user = userDao.selectByPrimaryKey(userAuthVO.getUserId());
        if (null == user) {
            user = new BaseUser();
            user.setId(ThreadLocalManager.getUserId());
            user.setName(ThreadLocalManager.getTokenContext().getName());
            if (configDTO.getNeedAudit() == 1) {
                user.setStatus(UserConstants.STATUS_DISABLE);
            } else {
                user.setStatus(UserConstants.STATUS_ENABLE);
            }
            user.setRole(UserConstants.ROLE_COMMON);
            user.setCreateTime(new Date());
            userDao.insertSelective(user);
        }
        user.setProjectAuth(this.authList2JSONString(userAuthVO.getProjectAuth()));
        return userDao.updateByPrimaryKeySelective(user);
    }

    /**
     * 获取某一用户（落地部署用）
     *
     * @param userId
     * @return
     */
    public UserInfoDTO getUserDetail(Integer userId) {
        BaseUser user = userDao.selectByPrimaryKey(userId);
        UserInfoDTO res = new UserInfoDTO();
        BeanUtils.copyProperties(user, res);
        if (StringUtils.isNotBlank(user.getBirthday())) {
            String[] str = user.getBirthday().split("-");
            res.setYear(str[0]);
            res.setMonth(str[1]);
        }
        res.setAuthList(this.getAuthList(user.getProjectAuth()));
        return res;
    }

    /**
     * 红包添加订单记录
     *
     * @return
     */
    public int insertOrder(UserBuyVO userBuyVO) {
        ProjectConfigDTO projectConfig = projectService.getProjectConfig(userBuyVO.getProjectId());
        if (projectConfig.getAllowRedPacket().equals(ProjectConstants.CLOSE)) {
            throw new ServiceException(ErrorCode.PROJECT_CONFIG_ERROR);
        }
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("projectId", userBuyVO.getProjectId());
        extraData.put("msg", "充值红包：" + userBuyVO.getOrderAmount());
        return this.insertPayOrder(userBuyVO, extraData);
    }

    /**
     * 发布添加订单记录
     *
     * @param userBuyVO
     * @return
     */
    public int insertIssueOrder(UserBuyVO userBuyVO) {
        ProjectConfigDTO projectConfig = projectService.getProjectConfig(userBuyVO.getProjectId());
        if (projectConfig.getAllowSmsAEmail().equals(ProjectConstants.CLOSE)) {
            throw new ServiceException(ErrorCode.PROJECT_CONFIG_ERROR);
        }
        Map<String, Object> extraData = new HashMap<>();
        extraData.put("projectId", userBuyVO.getProjectId());
        extraData.put("msg", "充值短信、邮箱：" + userBuyVO.getOrderAmount());
        return this.insertPayOrder(userBuyVO, extraData);
    }

    private int insertPayOrder(UserBuyVO userBuyVO, Map<String, Object> extraData) {
        Date now = new Date();
        BasePayOrder rdPayOrder = new BasePayOrder();
        //添加订单记录
        rdPayOrder.setOutTradeNo(CPayUtil.getTradeNo());
        rdPayOrder.setAmount(userBuyVO.getOrderAmount());
        rdPayOrder.setType(PayConstant.TYPE_RS);
        rdPayOrder.setPayWay(PayConstant.PAY_WAY_BALANCE);
        rdPayOrder.setPayType(PayConstant.PAY_TYPE_EXPEND);
        rdPayOrder.setUserId(ThreadLocalManager.getUserId().intValue());
        rdPayOrder.setStatus(PayConstant.STATUS_NO_PAY);
        rdPayOrder.setUpdateTime(now);
        rdPayOrder.setCreateTime(now);
        rdPayOrder.setExtraData(JSONObject.toJSONString(extraData));
        payOrderDao.insertSelective(rdPayOrder);
        return rdPayOrder.getId();
    }

    /**
     * 支付订单
     */
    public int insertBuyOrder(Integer id) {
        int res = 0;
        Date now = new Date();
        BasePayOrder payOrder = payOrderDao.selectByPrimaryKey(id);
        if (payOrder.getStatus().equals(PayConstant.STATUS_IS_PAY)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "订单已支付");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderAmount", payOrder.getAmount());
        params.put("type", PayConstant.TYPE_RS);
        params.put("extraData", JSONObject.toJSONString(payOrder));
        params.put("poundage", 0);
        Map<String, Object> extraData = (Map<String, Object>) JSON.parse(payOrder.getExtraData());
        params.put("msg", extraData.get("msg"));
        if (StringUtils.isBlank(extraData.get("projectId").toString())) {
            params.put("poundage", payOrder.getAmount());
            boolean flag = (boolean) authService.getResponseResult(params, authCreateOrderUrl);
            //判断是否购买成功
            //本地添加购买记录
            if (flag) {
                payOrder.setStatus(PayConstant.STATUS_IS_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                this.insertBuyRecord(payOrder);
                // 修改用户等级
                Calendar cal = Calendar.getInstance();
                BaseUser baseUser = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId().longValue());
                if (baseUser.getExpireTime() == null) {
                    cal.setTime(now);
                } else {
                    cal.setTime(baseUser.getExpireTime());
                }
                cal.add(Calendar.YEAR, 1);
                BaseUser user = new BaseUser();
                user.setId(ThreadLocalManager.getUserId());
                user.setRole(Integer.parseInt(extraData.get("vipType").toString()));
                user.setExpireTime(cal.getTime());
                userDao.updateByPrimaryKeySelective(user);
            } else {
                payOrder.setStatus(PayConstant.STATUS_WRONG_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                throw new ServiceException(ErrorCode.AMOUNT_NOT_ARRIVE);
            }
        } else {
            boolean payFlag = (boolean) authService.getResponseResult(params, authCreateOrderUrl);
            if (payFlag) {
                payOrder.setStatus(PayConstant.STATUS_IS_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                this.insertBuyRecord(payOrder);
            } else {
                payOrder.setStatus(PayConstant.STATUS_WRONG_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                throw new ServiceException(ErrorCode.AMOUNT_NOT_ARRIVE);
            }
        }
        return res;
    }

    /**
     * 购买成功
     *
     * @param payOrder
     */
    private BaseBuyRecord insertBuyRecord(BasePayOrder payOrder) {
        Date now = new Date();
        Map<String, Object> map = ((Map<String, Object>) JSON.parse(payOrder.getExtraData()));
        //添加购买记录
        BaseBuyRecord rdBuyRecord = new BaseBuyRecord();
        rdBuyRecord.setType(payOrder.getType());
        rdBuyRecord.setPayType(payOrder.getPayType());
        rdBuyRecord.setAmount(payOrder.getAmount());
        rdBuyRecord.setUserId(payOrder.getUserId());
        rdBuyRecord.setIsDelete(0);
        rdBuyRecord.setPayOrderId(payOrder.getId());
        rdBuyRecord.setCreateTime(now);
        // 判断用户是否购买
        rdBuyRecord.setMessage(map.get("msg").toString());
        if (map.get("projectId").toString() != null) {
            rdBuyRecord.setProjectId(Integer.parseInt(map.get("projectId").toString()));
        }
        buyRecordDao.insert(rdBuyRecord);
        return rdBuyRecord;
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    public int updateUser(BaseUser user) {
        BaseUser checkUser = userDao.selectByPrimaryKey(user.getId());
        if (checkUser != null) {
            // 删除redis中权限
            List<BaseProject> res = projectService.getProjectListByUserId(user.getId());
            if (res.size() > 0) {
                for (BaseProject p : res) {
                    redisUtil.hdel(RedisKeyConstants.projectKey(p.getId()), RedisKeyConstants.projectConfigKey(p.getId()));
                }
            }
            return userDao.updateByPrimaryKeySelective(user);
        } else {
            user.setStatus(UserConstants.STATUS_ENABLE);
            user.setCreateTime(new Date());
            return userDao.insertSelective(user);
        }
    }

    /**
     * 修改用户（落地部署用）
     *
     * @param user
     * @return
     */
    public int updateUserLocal(BaseUser user) {
        BaseUser checkUser = userDao.selectByPrimaryKey(user.getId());
        if (checkUser != null) {
            // 删除redis中权限
            List<BaseProject> res = projectService.getProjectListByUserId(user.getId());
            if (res.size() > 0) {
                for (BaseProject p : res) {
                    redisUtil.hdel(RedisKeyConstants.projectKey(p.getId()), RedisKeyConstants.projectConfigKey(p.getId()));
                }
            }
            return userDao.updateByPrimaryKeySelective(user);
        } else {
            user.setStatus(UserConstants.STATUS_ENABLE);
            ConfigDTO configDTO = projectService.getConfig();
            if (configDTO.getNeedAudit() == 1) {
                user.setStatus(UserConstants.STATUS_DISABLE);
            } else {
                user.setProjectAuth(this.authList2JSONString(configDTO.getProjectAuth()));

            }
            user.setCreateTime(new Date());
            return userDao.insertSelective(user);
        }
    }


    /**
     * 获取用户列表
     *
     * @param userSearchVO
     * @return
     */
    public PageList getUserList(UserSelectVO userSearchVO) {
        Page page = PageHelper.startPage(userSearchVO.getPageNum(), userSearchVO.getPageSize());
        if (!org.springframework.util.StringUtils.isEmpty(userSearchVO.getName())) {
            userSearchVO.setName("%" + userSearchVO.getName() + "%");
        }
        if (!org.springframework.util.StringUtils.isEmpty(userSearchVO.getPhone())) {
            userSearchVO.setPhone("%" + userSearchVO.getPhone() + "%");
        }
        if (!org.springframework.util.StringUtils.isEmpty(userSearchVO.getEmail())) {
            userSearchVO.setEmail("%" + userSearchVO.getEmail() + "%");
        }
        List<UserSelectDTO> list = userDao.getUserList(userSearchVO);
        for (UserSelectDTO selectDTO : list) {
            selectDTO.setAuthList(this.getAuthList(selectDTO.getProjectAuth()));
        }
        return new PageList(page);
    }

    /**
     * 获取用户消费记录
     *
     * @param param
     * @return
     */
    public PageList<Page> getUserBuyRecord(UserBuyRecordVO param) {
        Page page = PageHelper.startPage(param.getPageNum(), param.getPageSize());
        buyRecordDao.getBuyRecord(param);
        return new PageList<>(page);
    }

    /**
     * 获取用户消费记录（TestController用）
     *
     * @return
     */
    public PageList<Page> getUserBuyRecordTest() {
        BaseUser testUser = this.getTestUser();
        Page page = PageHelper.startPage(1, 10);
        buyRecordDao.getBuyRecordTest(testUser.getId());
        return new PageList<>(page);
    }

    /**
     * 申请退款
     *
     * @param reFundVO
     * @return
     */
    public void refundUserBalance(UserReFundVO reFundVO) {
        Date now = new Date();
        Integer projectId = reFundVO.getProjectId();
        // 创建退款手续订单
        int id = this.insertRefundOrder(reFundVO);
        BasePayOrder payOrder = payOrderDao.selectByPrimaryKey(id);
        if (payOrder.getStatus().equals(PayConstant.STATUS_IS_PAY)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "订单已支付");
        }

        // 退还到账户余额
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", payOrder.getUserId());
        params.put("type", PayConstant.TYPE_RS);
        Map<String, Object> extraData = (Map<String, Object>) JSON.parse(payOrder.getExtraData());
        params.put("extraData", JSONObject.toJSONString(payOrder));
        params.put("msg", extraData.get("msg").toString());

        // 退款金额
        BigDecimal orderAmount;
        BaseProjectPublishConfig publishConfig = null;// 发布的配置
        BaseRedPacketConfig redPacketConfig = null;// 红包的配置
        if (reFundVO.getRefundType().equals(UserConstants.REFUND_TYPE_SMS)) {
            publishConfig = issueService.getPublishConfig(projectId, IssueConstants.PUBLISH_TYPE_MESSAGE);
            orderAmount = publishConfig.getTotalAmount();
        } else if (reFundVO.getRefundType().equals(UserConstants.REFUND_TYPE_EMAIL)) {
            publishConfig = issueService.getPublishConfig(projectId, IssueConstants.PUBLISH_TYPE_EMAIL);
            orderAmount = publishConfig.getTotalAmount();
        } else {
            redPacketConfig = redPacketService.getRedPacketConfig(projectId);
            orderAmount = redPacketConfig.getTotalAmount();
        }
        BigDecimal refundAmount = orderAmount.multiply(new BigDecimal(0.99));
        params.put("orderAmount", refundAmount);
        boolean payFlag = (boolean) authService.getResponseResult(params, authPayOrderUrl);
        if (payFlag) {
            payOrder.setStatus(PayConstant.STATUS_IS_PAY);
            payOrder.setUpdateTime(now);
            payOrderDao.updateByPrimaryKey(payOrder);
            this.insertBuyRecord(payOrder);
            // 修改对应配置的金额
            if (!reFundVO.getRefundType().equals(UserConstants.REFUND_TYPE_RED_PACKET)) {
                publishConfig.setTotalAmount(BigDecimal.ZERO);
                publishConfig.setNum(0);
                publishConfigDao.updateByPrimaryKeySelective(publishConfig);
            } else {
                redPacketConfig.setTotalAmount(BigDecimal.ZERO);
                redPacketConfig.setNumber(0);
                redPacketConfigDao.updateByPrimaryKeySelective(redPacketConfig);
            }
        } else {
            payOrder.setStatus(PayConstant.STATUS_WRONG_PAY);
            payOrder.setUpdateTime(now);
            payOrderDao.updateByPrimaryKey(payOrder);
            throw new ServiceException(ErrorCode.AMOUNT_NOT_ARRIVE);
        }
    }

    private int insertRefundOrder(UserReFundVO reFundVO) {
        Integer projectId = reFundVO.getProjectId();
        BigDecimal orderAmount;
        String msg = "";
        if (reFundVO.getRefundType().equals(UserConstants.REFUND_TYPE_SMS)) {
            BaseProjectPublishConfig config = issueService.getPublishConfig(projectId, IssueConstants.PUBLISH_TYPE_MESSAGE);
            orderAmount = config.getTotalAmount();
            msg += "短信退款手续费：";
        } else if (reFundVO.getRefundType().equals(UserConstants.REFUND_TYPE_EMAIL)) {
            BaseProjectPublishConfig config = issueService.getPublishConfig(projectId, IssueConstants.PUBLISH_TYPE_EMAIL);
            orderAmount = config.getTotalAmount();
            msg += "邮件退款手续费：";
        } else {
            BaseRedPacketConfig config = redPacketService.getRedPacketConfig(projectId);
            orderAmount = config.getTotalAmount();
            msg += "红包退款手续费：";
        }
        BigDecimal poundage = orderAmount.multiply(new BigDecimal(0.01));
        BigDecimal refundAmount = orderAmount.multiply(new BigDecimal(0.99));
        msg += poundage.toString();
        msg += "，实际退还：" + refundAmount;

        Map<String, Object> extraData = new HashMap<>();
        extraData.put("projectId", reFundVO.getProjectId());
        extraData.put("msg", msg);

        // 创建订单,添加订单记录
        UserBuyVO userBuyVO = new UserBuyVO();
        userBuyVO.setOrderAmount(poundage);
        userBuyVO.setProjectId(projectId);
        return this.insertPayOrder(userBuyVO, extraData);
    }

    public boolean checkPhoneAvailable(String phoneNo, String oldPhoneNo) {
        //判断手机号是否被占用
        Example example = new Example(BaseUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("telephone", phoneNo);
        criteria.andNotEqualTo("telephone", oldPhoneNo);
        if (userDao.selectByExample(example).size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 审核用户（落地部属用）
     *
     * @param userAuditVO
     * @return
     */
    public int auditUser(UserAuditVO userAuditVO) {
        ConfigDTO configDTO = projectService.getConfig();
        BaseUser user = userDao.selectByPrimaryKey(userAuditVO.getUserId());
        if (null == user) {
            user = new BaseUser();
            user.setId(userAuditVO.getUserId());
            user.setName(userAuditVO.getName());
            user.setRole(UserConstants.ROLE_COMMON);
            user.setStatus(userAuditVO.getStatus());
            if (userAuditVO.getStatus() == 1) {
                user.setProjectAuth(this.authList2JSONString(configDTO.getProjectAuth()));
            }
            user.setCreateTime(new Date());
            userDao.insertSelective(user);
        }
        user.setStatus(userAuditVO.getStatus());
        return userDao.updateByPrimaryKeySelective(user);
    }

    /**
     * 获取测试运行用户（TestController的service用）
     *
     * @return
     */
    public BaseUser getTestUser() {
        Example example = new Example(BaseUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", "hundan");
        criteria.andEqualTo("telephone", "18852951585");
        return userDao.selectOneByExample(example);
    }
}
