package com.monetware.ringsurvey.business.service.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.PayConstant;
import com.monetware.ringsurvey.business.pojo.constants.RedisKeyConstants;
import com.monetware.ringsurvey.business.pojo.constants.UserConstants;
import com.monetware.ringsurvey.business.pojo.dto.user.UserBuyVO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserOrderDTO;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BaseBuyRecord;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BasePayOrder;
import com.monetware.ringsurvey.business.service.auth.AuthService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.ErrorCode;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2019-04-17
 */
@Slf4j
@Service
public class UserBuyService {

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BuyRecordDao buyRecordDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private LibraryDao libraryDao;

    @Autowired
    private LibraryRelationDao relationDao;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${outurl.authPayOrderUrl}")
    private String authPayOrderUrl;

    @Value("${outurl.authCreateOrderUrl}")
    private String authCreateOrderUrl;

    @Value("${outurl.authGetBalanceUrl}")
    private String authGetBalanceUrl;

    /**
     * ??????????????????
     *
     * @return
     */
    public int insertOrder(UserBuyVO userBuyVO) {
        BasePayOrder rdPayOrder = new BasePayOrder();
        Date now = new Date();
        //??????????????????
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        Map<String, Object> extraData = new HashMap<>();
        if (userBuyVO.getLibraryId() == null) {
            //????????????????????????????????????????????????
            if (userBuyVO.getType() < user.getRole()) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????????????????????????????????????????");
            }
            //??????????????????
            double price = permissionDao.selectByPrimaryKey(userBuyVO.getType()).getPrice();
            if (userBuyVO.getType().equals(UserConstants.ROLE_VIP)) {
                extraData.put("msg", "????????????????? ???????????????");
            } else if (userBuyVO.getType().equals(UserConstants.ROLE_CONPANY)) {
                extraData.put("msg", "????????????????? ???????????????");
            } else if (userBuyVO.getType().equals(UserConstants.ROLE_CUSTOM)) {
                extraData.put("msg", "????????????????? ???????????????");
            }
            extraData.put("vipType", userBuyVO.getType());
            rdPayOrder.setAmount(new BigDecimal(price));
        } else {
            // ????????????
            BaseQuestionnaireLibrary library = libraryDao.selectByPrimaryKey(userBuyVO.getLibraryId());
            if (userBuyVO.getUseFree()) {
                extraData.put("qnaireType", "FREE");
                rdPayOrder.setAmount(new BigDecimal(0));
            }else {
                extraData.put("qnaireType", "NO_FREE");
                rdPayOrder.setAmount(library.getPrice());
            }
            extraData.put("msg", "????????????:" + library.getName());
            extraData.put("questionnaireUser", library.getCreateUser());
            extraData.put("libraryId", library.getId());
            rdPayOrder.setAmount(new BigDecimal(String.valueOf(library.getPrice())));
        }
        rdPayOrder.setOutTradeNo(CPayUtil.getTradeNo());
        rdPayOrder.setType(PayConstant.TYPE_RS);
        rdPayOrder.setPayWay(PayConstant.PAY_WAY_BALANCE);
        rdPayOrder.setPayType(PayConstant.PAY_TYPE_EXPEND);
        rdPayOrder.setUserId(ThreadLocalManager.getUserId());
        rdPayOrder.setStatus(PayConstant.STATUS_NO_PAY);
        rdPayOrder.setUpdateTime(now);
        rdPayOrder.setCreateTime(now);
        rdPayOrder.setExtraData(JSONObject.toJSONString(extraData));
        payOrderDao.insertSelective(rdPayOrder);
        return rdPayOrder.getId();
    }

    /**
     * ????????????
     *
     * @param id
     * @return
     */
    public List<UserOrderDTO> getOrder(Integer id) {
        List<UserOrderDTO> result = new ArrayList<>();
        UserOrderDTO orderDTO = new UserOrderDTO();
        orderDTO.setBalance(this.getUserBalance());
        BaseUser user = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
        if (StringUtils.isNotBlank(user.getName())) {
            orderDTO.setAccount(user.getName());
        } else if (StringUtils.isNotBlank(user.getTelephone())) {
            orderDTO.setAccount(user.getTelephone());
        } else if (StringUtils.isNotBlank(user.getEmail())) {
            orderDTO.setAccount(user.getEmail());
        }
        BasePayOrder payOrder = payOrderDao.selectByPrimaryKey(id);
        orderDTO.setOrderNo(payOrder.getOutTradeNo());
        orderDTO.setOrderAmount(payOrder.getAmount());
        orderDTO.setName(((Map<String, Object>) JSON.parse(payOrder.getExtraData())).get("msg").toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDTO.setCreateTime(sdf.format(payOrder.getCreateTime()));
        result.add(orderDTO);
        return result;
    }

    /**
     * ????????????
     */
    public Integer insertBuyOrder(Integer id) {
        Date now = new Date();
        BasePayOrder payOrder = payOrderDao.selectByPrimaryKey(id);
        if (payOrder.getStatus().equals(PayConstant.STATUS_IS_PAY)) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderAmount", payOrder.getAmount());
        params.put("poundage", payOrder.getAmount());
        params.put("type", PayConstant.TYPE_RS);
        Map<String, Object> extraData = (Map<String, Object>) JSON.parse(payOrder.getExtraData());
        params.put("msg", extraData.get("msg"));
        params.put("extraData", JSONObject.toJSONString(payOrder));
        // TODO
        if (extraData.get("questionnaireUser") == null) {
            boolean flag = (boolean) authService.getResponseResult(params, authCreateOrderUrl);
            //????????????????????????
            //????????????????????????
            if (flag) {
                payOrder.setStatus(PayConstant.STATUS_IS_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                BaseBuyRecord buyRecord = this.insertBuyRecord(payOrder);
                // ??????????????????
                Calendar cal = Calendar.getInstance();
                //?????????????????????????????????
                BaseUser baseUser = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
                if (baseUser.getExpireTime() == null) {
                    cal.setTime(now);
                } else {
                    cal.setTime(baseUser.getExpireTime());
                }
                cal.add(Calendar.YEAR, 1);
                BaseUser user = new BaseUser();
                user.setId(ThreadLocalManager.getUserId());
                user.setRole((Integer) extraData.get("vipType"));
                user.setExpireTime(cal.getTime());
                userDao.updateByPrimaryKeySelective(user);
                Example example = new Example(BaseProject.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("createUser", user.getId());
                criteria.andEqualTo("isDelete", 0);
                List<BaseProject> projectList = projectDao.selectByExample(example);
                if (projectList.size() > 0) {
                    for (BaseProject project : projectList) {
                        ProjectConfigDTO configDto = new ProjectConfigDTO();
                        BaseProjectConfig config = JSONObject.parseObject(project.getConfig(), BaseProjectConfig.class);
                        BeanUtils.copyProperties(config, configDto);
                        configDto.setType(project.getType());
                        String role = extraData.get("vipType").toString();
                        configDto.setRole(role);
                        // ??????redis??????
                        redisUtil.hset(RedisKeyConstants.projectKey(project.getId()), RedisKeyConstants.projectConfigKey(project.getId()), configDto);
                    }
                }
            } else {
                payOrder.setStatus(PayConstant.STATUS_WRONG_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                throw new ServiceException(ErrorCode.AMOUNT_NOT_ARRIVE);
            }
        } else {
            // ??????????????????
            params.put("poundage", payOrder.getAmount().multiply(new BigDecimal("0.1")));
            boolean payFlag = (boolean) authService.getResponseResult(params, authCreateOrderUrl);
            params.put("orderAmount", payOrder.getAmount().multiply(new BigDecimal("0.9")));
            params.put("userIds", extraData.get("questionnaireUser"));
            boolean addFlag = (boolean) authService.getResponseResult(params, authPayOrderUrl);
            if (payFlag && addFlag) {
                payOrder.setStatus(PayConstant.STATUS_IS_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                this.insertBuyRecord(payOrder);
                payOrder.setPayType(PayConstant.PAY_TYPE_INCOME);
                payOrder.setUserId((Integer) extraData.get("questionnaireUser"));
                payOrder.setAmount(payOrder.getAmount().multiply(new BigDecimal(0.9)));
                this.insertBuyRecord(payOrder);
                // ?????????????????????????????????????????????????????????
                if ("FREE".equals(extraData.get("qnaireType"))) {
                    BaseUser baseUser = userDao.selectByPrimaryKey(ThreadLocalManager.getUserId());
                    baseUser.setUseFreeNum(baseUser.getUseFreeNum() + 1);
                    userDao.updateByPrimaryKey(baseUser);
                }
                BaseQuestionnaireLibrary lib = libraryDao.selectByPrimaryKey(extraData.get("libraryId"));
                lib.setSales(lib.getSales() + 1);
                libraryDao.updateByPrimaryKey(lib);
                BaseLibraryRelation relation = new BaseLibraryRelation();
                relation.setUserId(ThreadLocalManager.getUserId());
                relation.setType(1);
                relation.setLibraryId(lib.getId());
                relation.setProfit(payOrder.getAmount());
                relationDao.insertSelective(relation);
            }else{
                payOrder.setStatus(PayConstant.STATUS_WRONG_PAY);
                payOrder.setUpdateTime(now);
                payOrderDao.updateByPrimaryKey(payOrder);
                throw new ServiceException(ErrorCode.AMOUNT_NOT_ARRIVE);
            }
        }

        return (Integer) extraData.get("vipType");
    }

    /**
     * ????????????
     *
     * @param payOrder
     */
    private BaseBuyRecord insertBuyRecord(BasePayOrder payOrder) {
        Date now = new Date();
        //??????????????????
        BaseBuyRecord rdBuyRecord = new BaseBuyRecord();
        rdBuyRecord.setType(payOrder.getType());
        rdBuyRecord.setPayType(payOrder.getPayType());
        rdBuyRecord.setAmount(payOrder.getAmount());
        rdBuyRecord.setUserId(payOrder.getUserId());
        rdBuyRecord.setIsDelete(0);
        rdBuyRecord.setPayOrderId(payOrder.getId());
        rdBuyRecord.setCreateTime(payOrder.getCreateTime());
        rdBuyRecord.setMessage(((Map<String, Object>) JSON.parse(payOrder.getExtraData())).get("msg").toString());
        buyRecordDao.insert(rdBuyRecord);
        return rdBuyRecord;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public BigDecimal getUserBalance() {
        Map<String, Object> params = new HashMap<>();
        //TODO
        Map<String, Object> res = (Map<String, Object>) authService.getResponseResult(params, authGetBalanceUrl);
        return new BigDecimal(Double.parseDouble(res.get("balance").toString()));
    }

}
