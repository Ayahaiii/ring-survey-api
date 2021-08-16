package com.monetware.ringsurvey.business.service.project;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.constants.MonitorConstants;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.constants.SampleConstants;
import com.monetware.ringsurvey.business.pojo.dto.monitor.*;
import com.monetware.ringsurvey.business.pojo.dto.sample.InterviewerTravelDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SampleStatusDistributionDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserIdAndNameDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProject;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectProperty;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.business.pojo.vo.monitor.*;
import com.monetware.ringsurvey.business.pojo.vo.sample.InterviewerTravelVO;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.util.date.DateUtil;
import com.monetware.ringsurvey.system.util.file.FileUtil;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class MonitorService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectResponseDao responseDao;

    @Autowired
    private ProjectSampleStatusRecordDao recordDao;

    @Autowired
    private ProjectTeamUserDao projectTeamUserDao;

    @Autowired
    private ProjectSampleDao projectSampleDao;

    @Autowired
    private ProjectPropertyDao projectPropertyDao;

    @Autowired
    private ProjectResponsePositionDao positionDao;

    @Autowired
    private InterviewerTravelDao travelDao;

    @Autowired
    private ProjectService projectService;


    /**
     * 获取监控汇总数据
     *
     * @param baseVO
     * @return
     */
    public ProjectReportDTO getProjectReport(BaseVO baseVO) {
        ProjectReportDTO reportDTO = new ProjectReportDTO();
        if (baseVO.getCheckRole() == AuthorizedConstants.ROLE_ADMIN) {
            reportDTO = projectDao.getProjectReport(baseVO.getProjectId());
            reportDTO.setFileSizeStr(FileUtil.byteFormat(reportDTO.getFileSize(), true));
            reportDTO.setAvgFileSizeStr(FileUtil.byteFormat((reportDTO.getNumOfAnswer() == 0 ? 0L : reportDTO.getFileSize() / reportDTO.getNumOfAnswer() * 1L), true));
        } else {
            reportDTO = projectDao.getProjectReportByUserId(baseVO.getProjectId(), ThreadLocalManager.getUserId());
        }
        if (reportDTO.getNumOfSample() == 0) {
            reportDTO.setFinishRate(0D);
        } else {
            BigDecimal finishNum = new BigDecimal(reportDTO.getFinishNum());
            BigDecimal numOfSample = new BigDecimal(reportDTO.getNumOfSample());
            reportDTO.setFinishRate(finishNum.divide(numOfSample, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        }
        if (reportDTO.getResponseNum() == 0) {
            reportDTO.setSuccessRate(0D);
        } else {
            BigDecimal numOfAnswer = new BigDecimal(reportDTO.getNumOfAnswer());
            BigDecimal responseNum = new BigDecimal(reportDTO.getResponseNum());
            reportDTO.setSuccessRate(numOfAnswer.divide(responseNum, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue());
        }
        reportDTO.setTimeStr(DateUtil.secondToHourMinuteSecondEnStrByLong(reportDTO.getTimeLen()));

        Long tempTimeLen = 0L;
        ProjectConfigDTO config = projectService.getProjectConfig(baseVO.getProjectId());
        if (config.getMultipleQuestionnaire().equals(ProjectConstants.CLOSE)) {// 单问卷
            if (reportDTO.getNumOfAnswer() != 0) {
                tempTimeLen = reportDTO.getTimeLen() / reportDTO.getNumOfAnswer() * 1L;
            }
        } else {
            // 多问卷 除以样本已完成数量
            List<Integer> statusList = new ArrayList<>();
            statusList.add(SampleConstants.STATUS_FINISH);
            int sampleFinishNum = projectSampleDao.getSampleMapListByStatus(baseVO.getProjectId(), statusList).size();
            if (sampleFinishNum != 0) {
                tempTimeLen = reportDTO.getTimeLen() / sampleFinishNum * 1L;
            }
        }
        reportDTO.setAvgTimeStr(DateUtil.secondToHourMinuteSecondEnStrByLong(tempTimeLen));
        return reportDTO;
    }

    /**
     * 获取进度监控数据
     *
     * @param processVO
     * @return
     */
    public LinkedHashMap<String, Object> getAnswerProgress(AnswerProcessVO processVO) {
        processVO.setUserId(ThreadLocalManager.getUserId());
        Date now = new Date();
        List<String> list;
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap();
        List<Map<String, Object>> res;
        if (processVO.getEndTime() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 14);
            list = this.getLastNDays(calendar.getTime(), now, "D");
            processVO.setStartTime(calendar.getTime());
            processVO.setEndTime(now);
        } else {
            list = this.getLastNDays(processVO.getStartTime(), processVO.getEndTime(), "D");
        }
        res = responseDao.getAnswerProcessData(processVO);
        Map<String, Integer> totalMap = new TreeMap<>();
        for (int i = 0; i < res.size(); i++) {
            totalMap.put(res.get(i).get("yearDay").toString(), Integer.parseInt(res.get(i).get("totalCount").toString()));
        }
        List<Integer> countList = new ArrayList<>();
        for (String s : list) {
            if (totalMap.containsKey(s)) {
                countList.add(totalMap.get(s));
            } else {
                countList.add(0);
            }
        }
        linkedHashMap.put("xData", list);
        linkedHashMap.put("yData", countList);
        return linkedHashMap;
    }

    /**
     * 获取答卷进度筛选数据
     *
     * @param processVO
     * @return
     */
    public LinkedHashMap<String, Object> getAnswerTimeData(AnswerProcessVO processVO) {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap();
        List<Map<String, Object>> timeData = responseDao.getAnswerTimeData(processVO.getProjectId(), processVO.change(),
                processVO.getStartTime(), processVO.getEndTime(), processVO.getQnaireId());
        List<String> timeStrs = this.getLastNDays(processVO.getStartTime(), processVO.getEndTime(), processVO.getType());
        Map<String, Integer> totalMap = new TreeMap<>();
        for (int i = 0; i < timeData.size(); i++) {
            totalMap.put(timeData.get(i).get("timeStr").toString(), Integer.parseInt(timeData.get(i).get("totalCount").toString()));
        }
        List<Integer> countList = new ArrayList<>();
        for (String s : timeStrs) {
            if (totalMap.containsKey(s)) {
                countList.add(totalMap.get(s));
            } else {
                countList.add(0);
            }
        }
        linkedHashMap.put("xData", timeStrs);
        linkedHashMap.put("yData", countList);
        return linkedHashMap;
    }

    /**
     * 获取当前时间前N天日期
     *
     * @return
     */
    private List<String> getLastNDays(Date startDate, Date endDate, String type) {
        Date today = startDate; // calendar.getTime();
        SimpleDateFormat dateFormat;
        if ("D".equals(type)) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        }
        String endTime = dateFormat.format(today);

        // 返回的日期集合
        List<String> days = new ArrayList<String>();

        try {
            Date start = dateFormat.parse(endTime);
            Date end = dateFormat.parse(dateFormat.format(endDate));

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            if ("D".equals(type)) {
                // 日期加1(包含结束)
                tempEnd.add(Calendar.DATE, +1);
                while (tempStart.before(tempEnd)) {
                    days.add(dateFormat.format(tempStart.getTime()));
                    tempStart.add(Calendar.DAY_OF_YEAR, 1);
                }
            } else {
                // 小时加1(包含结束)
                tempEnd.add(Calendar.HOUR_OF_DAY, +1);
                while (tempStart.before(tempEnd)) {
                    days.add(dateFormat.format(tempStart.getTime()));
                    tempStart.add(Calendar.HOUR_OF_DAY, 1);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 获取样本经纬度坐标
     *
     * @param projectId
     * @return
     */
    public List<AnswerLonALatDTO> getAnswerLonAndLat(Integer projectId) {
        return responseDao.getSampleLonAndLatData(projectId);
    }

    /**
     * 获取样本使用动态
     *
     * @param statusUseVO
     * @return
     */
    public LinkedHashMap<String, Object> getSampleStatusUseList(SampleStatusUseVO statusUseVO) {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        List<SampleStatusUseDTO> datas = recordDao.getSampleStatusUseList(statusUseVO.getProjectId(), statusUseVO.change(),
                statusUseVO.getStartTime(), statusUseVO.getEndTime());
        List<String> timeStrs = this.getLastNDays(statusUseVO.getStartTime(), statusUseVO.getEndTime(), statusUseVO.getType());
        if (datas.size() > 0) {
            List<Integer> statusKeys = SampleConstants.STATUS_KEYS;
            for (Integer s : statusKeys) {
                Map<String, Integer> totalMap = new TreeMap<>();
                List<SampleStatusUseDTO> tempDatas = new ArrayList<>();
                for (SampleStatusUseDTO d : datas) {
                    if (s.equals(d.getStatus())) {
                        totalMap.put(d.getTimeStr(), d.getTotalCount());
                    } else {
                        tempDatas.add(d);
                    }
                }
                List<Integer> countList = new ArrayList<>();
                for (String t : timeStrs) {
                    if (totalMap.containsKey(t)) {
                        countList.add(totalMap.get(t));
                    } else {
                        countList.add(0);
                    }
                }
                datas = tempDatas;
                res.put("yStatus_" + s, countList);
            }
        }
        res.put("xData", timeStrs);
        return res;
    }

    //============================================ lu Begin =======================================

    /**
     * @Author: lu
     * @Date: 2020/4/1  上午11:11
     * @Description:获取样本绩效动态
     **/
    public LinkedHashMap<String, Object> getSampleCompleteList(SampleCompleteVO param) {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        //查询项目团队表里角色是访问员的id和name list
        List<UserIdAndNameDTO> userList = projectTeamUserDao.getUserIdAndName(param.getProjectId(), param.getUserId());
        //获取项目团队表里角色是访问员的id list
        List<Integer> interviewerIdList = projectTeamUserDao.getInterviewerIdList(param.getProjectId(), param.getUserId());
        List<SampleCompleteDTO> datas = recordDao.getSampleCompleteList(param.getProjectId(), param.change(),
                param.getStartTime(), param.getEndTime(), interviewerIdList);
        //x轴
        List<String> timeStrs = this.getLastNDays(param.getStartTime(), param.getEndTime(), param.getType());
        //y轴
        List<HashMap> yData = new ArrayList<>();
        //table 数据
        List<HashMap> tableList = new ArrayList<>();

        if (datas.size() > 0) {
            for (UserIdAndNameDTO dto : userList) {
                HashMap<String, Object> dataMap = new HashMap<>();
                String interviewerName = dto.getName();
                HashMap<String, Object> tableData = new HashMap<>();
                tableData.put("name", interviewerName);
                Map<String, Integer> totalMap = new TreeMap<>();
                List<SampleCompleteDTO> tempDatas = new ArrayList<>();
                for (SampleCompleteDTO d : datas) {
                    if (dto.getId().equals(d.getInterviewerId())) {
                        totalMap.put(d.getTimeStr(), d.getTotalCount());
                    } else {
                        tempDatas.add(d);
                    }
                }
                List<Integer> countList = new ArrayList<>();
                for (String t : timeStrs) {
                    //set ydata 和tableData
                    if (totalMap.containsKey(t)) {
                        countList.add(totalMap.get(t));
                        tableData.put(t, totalMap.get(t));
                    } else {
                        countList.add(0);
                        tableData.put(t, 0);
                    }
                }
                //存放y轴数据
                dataMap.put(interviewerName, countList);
                yData.add(dataMap);
                //存放table数据
                tableList.add(tableData);
            }
        }
        res.put("tableData", tableList);
        res.put("yData", yData);
        res.put("xData", timeStrs);
        return res;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/1  下午7:29
     * @Description:获取样本状态分布
     **/
    public LinkedHashMap getSampleStatusDistribution(Integer projectId) {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        //获取调查类型
        String cType = projectDao.selectByPrimaryKey(projectId).getType();
        if (cType.equals(ProjectConstants.PROJECT_TYPE_CATI)) {
            res.put("xData", MonitorConstants.CATI_STATUS_INFO);
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CAPI)) {
            res.put("xData", MonitorConstants.CAPI_STATUS_INFO);
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
            res.put("xData", MonitorConstants.CAWI_STATUS_INFO);
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CADI)) {
            res.put("xData", MonitorConstants.CADI_STATUS_INFO);
        } else {
            res.put("xData", MonitorConstants.CAXI_STATUS_INFO);
        }
        //获取各样本状态数量总数
        List<SampleStatusDistributionDTO> countList = projectSampleDao.getSampleStatusDistribution(projectId);
        //不同调查类型显示不同状态
        List<Integer> statusKeys;
        if (cType.equals(ProjectConstants.PROJECT_TYPE_CATI)) {
            statusKeys = SampleConstants.CATI_STATUS_KEYS;
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CAPI)) {
            statusKeys = SampleConstants.CAPI_STATUS_KEYS;
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
            statusKeys = SampleConstants.CAWI_STATUS_KEYS;
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CADI)) {
            statusKeys = SampleConstants.CADI_STATUS_KEYS;
        } else {
            statusKeys = SampleConstants.CAXI_STATUS_KEYS;
        }
        //赋y轴值
        LinkedList<Integer> yData = new LinkedList<>();
        //计算样本总数
        Example example = new Example(BaseProjectSample.class);
        example.setTableName(ProjectConstants.getSampleTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete", 0);
        criteria.andEqualTo("ifVirtual", SampleConstants.VIRTUAL_NO);
        Integer count = projectSampleDao.selectCountByExample(example);
        yData.add(count == null ? 0 : count);

        if (countList != null && countList.size() > 0) {
            for (Integer s : statusKeys) {
                int statusCount = 0;
                for (SampleStatusDistributionDTO sd : countList) {
                    if (s.equals(sd.getStatus())) {
                        statusCount = sd.getCount().intValue();
                        break;
                    }
                }
                yData.add(statusCount);
            }
        }
        res.put("yData", yData);
        return res;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  上午10:42
     * @Description:查看样本是否启动省市县
     **/
    public Boolean ifRegionSelection(Integer projectId) {
        BaseProjectProperty bpp = new BaseProjectProperty();
        bpp.setProjectId(projectId);
        bpp = projectPropertyDao.selectOne(bpp);
        if (bpp != null) {
            String useProperty = bpp.getUseProperty();
            if (!StringUtils.isEmpty(useProperty) && useProperty.contains("province") && useProperty.contains("city") && useProperty.contains("district")) {
                //启动省市县属性
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  上午10:22
     * @Description:获取地图坐标信息(前100)
     **/
    public List<ResponseLocationDTO> getResponseLocation(Integer projectId) {
        return responseDao.getResponseLocation(projectId);
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  上午10:58
     * @Description:通过省市区获取样本数量
     **/
    public List<GetSampleCountByAddressDTO> getSampleCountByAddress(GetSampleCountByAddressVO param) {
        if (param.getProvince() != null && !StringUtils.isEmpty(param.getProvince())) {
            if (param.getCity() != null && !StringUtils.isEmpty(param.getCity())) {
                if (param.getDistrict() != null && !StringUtils.isEmpty(param.getDistrict())) {
                    //显示当前区样本位置
                    return projectSampleDao.getSampleLocation(param.getProjectId(), param.getProvince(), param.getCity(), param.getDistrict());
                } else {
                    //显示当前市各区样本数量汇总
                    List<GetSampleCountByAddressDTO> list = projectSampleDao.getSampleCountByCity(param.getProjectId(), param.getProvince(), param.getCity());
                    for (GetSampleCountByAddressDTO a : list) {
                        String address = StringUtils.isEmpty(a.getDistrict()) ? a.getCity() : a.getDistrict();
                        LonAndLat ll = this.getLonAndLatByAddress(address);
                        a.setLat(ll.getLat());
                        a.setLon(ll.getLon());
                    }
                    return list;
                }
            } else {
                //显示当前省份各市样本数量汇总
                List<GetSampleCountByAddressDTO> list = projectSampleDao.getSampleCountByProvince(param.getProjectId(), param.getProvince());
                for (GetSampleCountByAddressDTO a : list) {
                    String address = StringUtils.isEmpty(a.getCity()) ? a.getProvince() : a.getCity();
                    LonAndLat ll = this.getLonAndLatByAddress(address);
                    a.setLat(ll.getLat());
                    a.setLon(ll.getLon());
                }
                return list;
            }
        } else {
            //获取各省份样本数量汇总
            List<GetSampleCountByAddressDTO> list = projectSampleDao.getSampleByCountry(param.getProjectId());
            for (GetSampleCountByAddressDTO a : list) {
                if (StringUtils.isEmpty(a.getProvince())) {
                    a.setLat(MonitorConstants.LAT);
                    a.setLon(MonitorConstants.LON);
                } else {
                    LonAndLat ll = this.getLonAndLatByAddress(a.getProvince());
                    a.setLat(ll.getLat());
                    a.setLon(ll.getLon());
                }
            }
            return list;
        }
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午2:14
     * @Description:获取指标动态
     **/
    public LinkedHashMap<String, Object> getIndexDynamic(GetIndexDynamicVO param) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        //获取样本总数
        Example example = new Example(BaseProjectSample.class);
        example.setTableName(ProjectConstants.getSampleTableName(param.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        Integer totalCount = projectSampleDao.selectCountByExample(example);
        //获取指标动态数据
        List<IndexDynamicDTO> dy = recordDao.getIndexDynamic(param.getProjectId(), param.change(), param.getStartTime(),
                param.getEndTime(), totalCount);
        //获取x轴数据
        List<String> timeStrs = this.getLastNDays(param.getStartTime(), param.getEndTime(), param.getType());
        //存放数据
        List<Float> useRateList = new ArrayList<>();
        List<Float> validRateList = new ArrayList<>();
        List<Float> successRateList = new ArrayList<>();
        List<Float> validSuccessRateList = new ArrayList<>();
        List<Float> refuseRateList = new ArrayList<>();
        List<Float> validRefuseRateList = new ArrayList<>();
        List<Float> auditRateList = new ArrayList<>();
        List<Float> auditSuccessRateList = new ArrayList<>();

        for (String t : timeStrs) {
            for (IndexDynamicDTO idd : dy) {
                if (idd.getTimeStr().equals(t)) {
                    useRateList.add(idd.getUseRate());
                    validRateList.add(idd.getValidRate());
                    successRateList.add(idd.getSuccessRate());
                    validSuccessRateList.add(idd.getValidSuccessRate());
                    refuseRateList.add(idd.getRefuseRate());
                    validRefuseRateList.add(idd.getValidRefuseRate());
                    auditRateList.add(idd.getAuditRate());
                    auditSuccessRateList.add(idd.getSuccessRate());
                } else {
                    useRateList.add(0F);
                    validRateList.add(0F);
                    successRateList.add(0F);
                    validSuccessRateList.add(0F);
                    refuseRateList.add(0F);
                    validRefuseRateList.add(0F);
                    auditRateList.add(0F);
                    auditSuccessRateList.add(0F);
                }
            }
        }
        result.put("useRateList", useRateList);
        result.put("validRateList", validRateList);
        result.put("successRateList", successRateList);
        result.put("validSuccessRateList", validSuccessRateList);

        result.put("refuseRateList", refuseRateList);
        result.put("validRefuseRateList", validRefuseRateList);
        result.put("auditRateList", auditRateList);
        result.put("auditSuccessRateList", auditSuccessRateList);

        result.put("xData", timeStrs);
        return result;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午5:37
     * @Description:获取仪表盘
     **/
    public SampleDashboardDTO getSampleDashboard(Integer projectId) {
        return projectSampleDao.getSampleDashboard(projectId);
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午7:16
     * @Description:获取浏览器参数
     **/
    public LinkedHashMap<String, Object> getBrowserParam(GetBrowserParamVO param) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        List<GetBrowserParamDTO> tableData = responseDao.getCountWithTypeAndVersion(param);
        //1.浏览器分布
        List<String> xData = new ArrayList<>();
        List<Integer> yData = new ArrayList<>();
        //2.Chrome的版本分布
        List<String> xChromeData = new ArrayList<>();
        List<Integer> yChromeData = new ArrayList<>();
        //3.QQ浏览器的版本分布
        List<String> xQqData = new ArrayList<>();
        List<Integer> yQqData = new ArrayList<>();
        //4.Safari  的版本分布
        List<String> xSafariData = new ArrayList<>();
        List<Integer> ySafariData = new ArrayList<>();

        //5.获取数据
        List<GetBrowserParamDTO> gdList = responseDao.getBrowserParam(param);
        List<GetBrowserParamDTO> chromeList = responseDao.getChromeVersionParam(param);
        List<GetBrowserParamDTO> qqList = responseDao.getQqVersionParam(param);
        List<GetBrowserParamDTO> safariList = responseDao.getSafariVersionParam(param);

        //6.处理浏览器分布
        if (gdList != null && gdList.size() > 0) {
            for (int i = 0; i < gdList.size(); i++) {
                yData.add(gdList.get(i).getCount());
                if (StringUtils.isEmpty(gdList.get(i).getExplorerType())) {
                    xData.add("未知");
                } else {
                    xData.add(gdList.get(i).getExplorerType());
                }
            }
        }
        result.put("xData", xData);
        result.put("yData", yData);

        //7.处理Chrome的版本分布
        if (chromeList != null && chromeList.size() > 0) {
            for (int i = 0; i < chromeList.size(); i++) {
                yChromeData.add(chromeList.get(i).getCount());
                if (StringUtils.isEmpty(chromeList.get(i).getExplorerVersion())) {
                    xChromeData.add("未知");
                } else {
                    xChromeData.add(chromeList.get(i).getExplorerVersion());
                }
            }
        }
        result.put("xChromeData", xChromeData);
        result.put("yChromeData", yChromeData);

        //8.处理QQ浏览器的版本分布
        if (qqList != null && qqList.size() > 0) {
            for (int i = 0; i < qqList.size(); i++) {
                yQqData.add(qqList.get(i).getCount());
                if (StringUtils.isEmpty(qqList.get(i).getExplorerVersion())) {
                    xQqData.add("未知");
                } else {
                    xQqData.add(qqList.get(i).getExplorerVersion());
                }
            }
        }
        result.put("xQqData", xQqData);
        result.put("yQqData", yQqData);

        //9.处理Safari的版本分布
        if (safariList != null && safariList.size() > 0) {
            for (int i = 0; i < safariList.size(); i++) {
                ySafariData.add(safariList.get(i).getCount());
                if (StringUtils.isEmpty(safariList.get(i).getExplorerVersion())) {
                    xSafariData.add("未知");
                } else {
                    xSafariData.add(safariList.get(i).getExplorerVersion());
                }
            }
        }
        result.put("xSafariData", xSafariData);
        result.put("ySafariData", ySafariData);

        //10.浏览器版本类型统计
        result.put("tableData", tableData);

        return result;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午7:44
     * @Description:获取来源报告
     **/
    public List<GetSourceReportDTO> getSourceReport(Integer projectId) {
        List<GetSourceReportDTO> list = responseDao.getSourceReport(projectId);
        return list;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/3  下午1:29
     * @Description:获取地图报告
     **/
    public List<GetMapReportDTO> getMapReport(GetMapReportVO param) {
        return positionDao.getMapReport(param);
    }

    /**
     * @Author: lu
     * @Date: 2020/4/3  下午3:38
     * @Description:获取地图报告详细信息
     **/
    public List<GetMapReportDetailDTO> getMapReportDetail(GetMapReportDetailVO param) {
        return positionDao.getMapReportDetail(param);
    }

    /**
     * @Author: lu
     * @Date: 2020/4/13  下午1:33
     * @Description:获取当前项目访员信息
     **/
    public List<UserIdAndNameDTO> getInterviewers(Integer projectId) {
        //查询项目团队表里角色是访问员的id和name list
        List<UserIdAndNameDTO> result = projectTeamUserDao.getUserIdAndName(projectId, null);
        return result;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/14  下午3:38
     * @Description:按地区名获取经纬度
     **/
    public LonAndLat getLonAndLatByAddress(String address) {
        LonAndLat re = new LonAndLat();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "https://api.map.baidu.com/geocoder?key=q8b18hgUW3UCLoyXtKk7l4w7&address=" + address;
        String result = restTemplate.postForEntity(url, requestEntity, String.class).getBody();
        JSONObject obj = XML.toJSONObject(result).getJSONObject("GeocoderSearchResponse");
        if (!StringUtils.isEmpty(obj.get("result").toString())) {
            double lon = obj.getJSONObject("result").getJSONObject("location").getDouble("lng"); // 经度
            double lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat"); // 纬度
            re.setLat(String.valueOf(lat));
            re.setLon(String.valueOf(lon));
        }
        return re;
    }

    /**
     * @Author: lu
     * @Date: 2020/4/14  下午7:23
     * @Description:qqq
     **/
    public List<GetQuestionnaireListDTO> getQuestionnaireList(Integer projectId) {
        return responseDao.getQuestionnaireList(projectId);
    }

    /**
     * @Author: lu
     * @Date: 2020/4/27  下午3:09
     * @Description:根据项目类型获取状态列表
     **/
    public List<Integer> getStatusList(Integer projectId) {
        //获取调查类型
        String cType = projectDao.selectByPrimaryKey(projectId).getType();
        if (cType.equals(ProjectConstants.PROJECT_TYPE_CATI)) {
            return SampleConstants.CATI_STATUS_KEYS;
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CAPI)) {
            return SampleConstants.CAPI_STATUS_KEYS;
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CAWI)) {
            return SampleConstants.CAWI_STATUS_KEYS;
        } else if (cType.equals(ProjectConstants.PROJECT_TYPE_CADI)) {
            return SampleConstants.CADI_STATUS_KEYS;
        } else {
            return SampleConstants.CAXI_STATUS_KEYS;
        }
    }

    /**
     * 样本状态统计
     *
     * @param statisticsVO
     * @return
     */
    public SampleStatisticsDTO getSampleStatusStatistics(SampleStatusStatisticsVO statisticsVO) {
        Integer projectId = statisticsVO.getProjectId();
        SampleStatisticsDTO res = new SampleStatisticsDTO();
        List<Integer> statusList = this.getStatusList(projectId);
        res.setStatusList(statusList);

        Page page = PageHelper.startPage(statisticsVO.getPageNum(), statisticsVO.getPageSize());
        projectSampleDao.getSampleStatusStatistics(statisticsVO);
        res.setDataList(new PageList<>(page));
        return res;
    }
    //============================================ lu End =========================================

    /**
     * 单个访员轨迹
     *
     * @param travelVO
     * @return
     */
    public List<InterviewerTravelDTO> getInterviewerTravelList(InterviewerTravelVO travelVO) {
        return travelDao.getInterviewerTravelList(travelVO);
    }

    /**
     * 项目访员最后轨迹列表
     *
     * @param travelVO
     * @return
     */
    public List<InterviewerTravelDTO> getInterviewersLastedTravel(InterviewerTravelVO travelVO) {
        return travelDao.getInterviewersLastedTravel(travelVO);
    }

}
