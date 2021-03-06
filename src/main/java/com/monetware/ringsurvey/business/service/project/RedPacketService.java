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
     * ??????????????????
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
     * ??????????????????
     *
     * @return
     */
    public int insertOrder(UserBuyVO userBuyVO) {
        return userService.insertOrder(userBuyVO);
    }

    /**
     * ????????????
     */
    public int insertBuyOrder(Integer id) {
        return userService.insertBuyOrder(id);
    }

    /**
     * ????????????????????????
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
     * ????????????
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
     * ???????????????????????????
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
     * ??????????????????/????????????
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
        // ????????????????????????
        // send_name???????????????????????????32??????(????????????????????????)
        int sendNameLen = this.getStringLength(redPacketConfigVO.getSendName());
        if (sendNameLen >= 32) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????????????????????????????32?????????");
        }
        // act_name????????????,????????????32?????????(????????????????????????)
        int actNameLen = this.getStringLength(redPacketConfigVO.getActName());
        if (actNameLen >= 32) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "?????????????????????????????????32?????????");
        }
        // wishing???????????????,????????????128?????????(????????????????????????)
        int wishLen = this.getStringLength(redPacketConfigVO.getWish());
        if (wishLen >= 128) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "?????????????????????????????????128?????????");
        }

        BaseRedPacketConfig redPacketConfig = new BaseRedPacketConfig();
        BeanUtils.copyProperties(redPacketConfigVO, redPacketConfig);

        // ??????????????????????????????
        BigDecimal totalAmount = redPacketConfigVO.getTotalAmount();
        BigDecimal number = new BigDecimal(redPacketConfigVO.getNumber());
        BigDecimal mixAmount = new BigDecimal(1);
        BigDecimal perAmount = mixAmount;
        if (number.compareTo(BigDecimal.ZERO) != 0) {
            perAmount = totalAmount.divide(number, 2, RoundingMode.HALF_UP);
        }
        redPacketConfig.setPerAmount(perAmount);

        // ??????????????????????????????
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
     * ????????????????????????
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
     * ??????/??????????????????
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
            // ?????????????????????
            redPacketRecordDao.createCustomTable(ProjectConstants.getRedPackRecordTableName(statusVO.getProjectId()));
        }
        ProjectConfigDTO dto = new ProjectConfigDTO();
        BeanUtils.copyProperties(configDTO, dto);
        // ??????redis??????
        return redisUtil.hset(RedisKeyConstants.projectKey(projectId), RedisKeyConstants.projectConfigKey(projectId), dto);
    }


    /**
     * ??????????????????
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
     * ??????????????????
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
     * ????????????
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
                // ??????????????????????????????????????????
                boolean flag = this.addMoneyAccount(redPacketConfig, record.getTotalAmount().divide(new BigDecimal(100)));// ??????????????????
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

            // ???????????? ????????????
            if (recordAuditVO.getAuditResult().equals(RedPacketConstants.AUDIT_SUCCESS)) {
                BaseResponse response = responseService.getResponse(recordAuditVO.getProjectId(), null, record.getResponseGuid());
                if (StringUtils.isNotBlank(redPacketConfig.getAreaLimit())) {
                    String respIp = response.getIpAddress();
                    boolean ipAreaLimit = this.checkIpAreaLimit(redPacketConfig.getAreaLimit(), respIp);
                    if (!ipAreaLimit) {
                        // ???????????????????????????
                        boolean returnFlag = this.addMoneyAccount(redPacketConfig, record.getTotalAmount().divide(new BigDecimal(100)));// ??????????????????
                        if (!returnFlag) {
                            throw new ServiceException(ErrorCode.PACKET_RETURN_ERROR);
                        }
                        throw new ServiceException(ErrorCode.PACKET_AREA_LIMIT_ERROR);
                    }
                }
                // ????????????
                this.sendRedPacket(record, response, redPacketConfig);
            }
        }

        return row;
    }

    /**
     * ??????????????????
     *
     * @param record
     * @param response
     * @param redPacketConfig
     */
    public void sendRedPacket(BaseRedPacketRecord record, BaseResponse response, BaseRedPacketConfig redPacketConfig) {
        // ????????????
        Map<String, String> resultMap = this.sendRedPacketToWeiXin(record.getOpenId(), record.getMchBillNo(),
                record.getTotalAmount().intValue(), redPacketConfig);
        record.setResult(resultMap.get("return_msg"));
        if (resultMap.get("result_code").equals("SUCCESS")) {
//            this.subtractMoneyAccount(redPacketConfig, record.getTotalAmount());// ??????????????????
            record.setStatus(RedPacketConstants.SEND_SUCCESS);// ???????????? ??????
            response.setIsSendRedpacket(RedPacketConstants.YES);// ?????? ???????????????
        } else {
            record.setStatus(RedPacketConstants.SEND_FAIL);// ???????????? ??????
            response.setIsSendRedpacket(RedPacketConstants.NO);// ?????? ???????????????
            // ?????????????????????????????????
            boolean flag = this.addMoneyAccount(redPacketConfig, record.getTotalAmount().divide(new BigDecimal(100)));
            if (!flag) {
                throw new ServiceException(ErrorCode.PACKET_RETURN_ERROR);
            }
        }
        // ??????????????????
        this.saveRedPacketRecord(record, redPacketConfig.getProjectId());
        // ??????
        response.setDynamicTableName(ProjectConstants.getResponseTableName(redPacketConfig.getProjectId()));
        responseDao.updateByPrimaryKeySelective(response);
    }

    /**
     * ????????????????????????
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
     * ????????????
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
     * ????????????????????????????????????????????????????????????????????????2???
     *
     * @param value ??????????????????
     * @return ??????????????????
     */
    public static int getStringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* ?????????????????????????????????????????????????????????????????????????????????2????????????1 */
        if (StringUtils.isNotBlank(value)) {
            for (int i = 0; i < value.length(); i++) {
                /* ?????????????????? */
                String temp = value.substring(i, i + 1);
                /* ??????????????????????????? */
                if (temp.matches(chinese)) {
                    /* ?????????????????????2 */
                    valueLength += 2;
                } else {
                    /* ?????????????????????1 */
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    /**
     * ??????????????????
     *
     * @param listVO
     * @return
     */
    public int exportRedRacketRecord(RedPacketRecordListVO listVO) throws Exception {
        Integer projectId = listVO.getProjectId();
        //
        List<RedPacketRecordListDTO> data = redPacketRecordDao.getRedPacketRecordList(listVO);
        JSONArray array = JSON.parseArray(JSON.toJSONString(data));
        // ????????????
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
        exportHistory.setDescription("??????????????????");
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(new Date());
        int row = exportDao.insert(exportHistory);
        if (row > 0) {
            // ?????????????????????
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
     * ????????????????????????
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
     * ????????????????????????
     *
     * @param delVO
     * @return
     */
    public int deleteDownFile(RedPacketDeleteVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/redpacket/";
        // ?????????????????????
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
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????");
        }
    }

    /**
     * ??????????????????????????????
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
     * ??????????????????
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
     * check????????????
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
