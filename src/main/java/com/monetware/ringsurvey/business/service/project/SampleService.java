package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.*;
import com.monetware.ringsurvey.business.pojo.dto.history.ExportHistoryDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.*;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.history.HistoryListVO;
import com.monetware.ringsurvey.business.pojo.vo.sample.*;
import com.monetware.ringsurvey.business.service.DataBaseService;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.codec.UUIDUtil;
import com.monetware.ringsurvey.system.util.date.DateUtil;
import com.monetware.ringsurvey.system.util.file.*;
import com.monetware.threadlocal.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class SampleService {

    @Autowired
    private ProjectSampleDao sampleDao;

    @Autowired
    private ProjectSampleStatusRecordDao recordDao;

    @Autowired
    private InfoProvinceCityDao cityDao;

    @Autowired
    private ProjectSampleAssignDao assignmentDao;

    @Autowired
    private ProjectSampleAddressDao addressDao;

    @Autowired
    private ProjectSampleContactDao contactDao;

    @Autowired
    private ProjectSampleTouchDao touchDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private ProjectPropertyDao propertyDao;

    @Autowired
    private ProjectPropertyTemplateDao templateDao;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @Value("${fileUrl.upload}")
    private String filePath;

    @Autowired
    private ProjectResponseDao responseDao;

    @Autowired
    private ProjectSampleStatusRecordDao statusRecordDao;

    @Autowired
    private NumberRuleDao numberRuleDao;

    @Autowired
    private DataBaseService dataBaseService;

    @Autowired
    private ProjectSamplePoolDao samplePoolDao;

    /**
     * ??????????????????
     *
     * @param townVO
     * @return
     */
    public List<String> getTownNameList(TownVO townVO) {
        if (townVO.getCityName().equals("?????????")
                || townVO.getCityName().equals("?????????")
                || townVO.getCityName().equals("?????????")
                || townVO.getCityName().equals("?????????")) {
            townVO.setCityName("?????????");
        }
        return cityDao.getTownNameList(townVO);
    }

    /**
     * ???????????????????????????
     *
     * @param projectSampleVO
     * @return
     */
    public Integer saveProjectSample(ProjectSampleVO projectSampleVO) {
        Date now = new Date();
        //??????code????????????
        if (sampleDao.checkSampleExistByCode(projectSampleVO.getProjectId(), projectSampleVO.getCode(), projectSampleVO.getId()) > 0) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "?????????????????????");
        }

        // ????????????????????????
        Integer userRole = projectService.getProjectOwnerRole(projectSampleVO.getProjectId());
        if (userRole.equals(UserConstants.ROLE_COMMON) || userRole.equals(UserConstants.ROLE_VIP) || userRole.equals(UserConstants.ROLE_CONPANY)) {
            // ????????????????????????
            Example ex = new Example(BaseProjectSample.class);
            ex.setTableName(ProjectConstants.getSampleTableName(projectSampleVO.getProjectId()));
            int count = sampleDao.selectCountByExample(ex);
            if (count >= SampleConstants.TOP_LIMIT) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
            }
        }

        BaseProjectSample sample = new BaseProjectSample();
        BeanUtils.copyProperties(projectSampleVO, sample);
        sample.setDynamicTableName(ProjectConstants.getSampleTableName(projectSampleVO.getProjectId()));
        List<BaseProjectSampleStatusRecord> statusRecords = new ArrayList<>();
        if (projectSampleVO.getId() == null) {
            String sampleGuid = UUIDUtil.getRandomUUID();
            sample.setSampleGuid(sampleGuid);
            sample.setIfVirtual(SampleConstants.VIRTUAL_NO);
            sample.setCreateUser(ThreadLocalManager.getUserId());
            sample.setCreateTime(now);
            sample.setStatus(SampleConstants.STATUS_INIT);
            sample.setLastModifyTime(now);
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            sample.setIsDelete(ProjectConstants.DELETE_NO);
            sample.setRandomNumber((int) ((Math.random() * 9 + 1) * 10000000));
            statusRecords.add(new BaseProjectSampleStatusRecord(projectSampleVO.getProjectId(), sampleGuid, sample.getStatus(), now));
            // ?????????????????????????????????????????????
            List<BaseSampleAssignment> assignmentList = new ArrayList<>();
            if (projectSampleVO.getCheckRole() == AuthorizedConstants.ROLE_INTERVIEWER) {
                BaseProjectTeamUser teamUser = teamUserDao.getTeamUser(projectSampleVO.getProjectId(), ThreadLocalManager.getUserId());
                BaseSampleAssignment assignment = new BaseSampleAssignment();
                assignment.setDynamicTableName(ProjectConstants.getSampleAssignmentTableName(projectSampleVO.getProjectId()));
                assignment.setType(1);
                assignment.setTeamUserId(teamUser.getId());
                assignment.setCreateUser(ThreadLocalManager.getUserId());
                assignment.setCreateTime(now);
                assignment.setLastModifyUser(ThreadLocalManager.getUserId());
                assignment.setLastModifyTime(now);
                assignment.setSampleGuid(sampleGuid);
                assignment.setIsDelete(ProjectConstants.DELETE_NO);
                assignmentList.add(assignment);
                // ??????????????????????????????
                sample.setStatus(SampleConstants.STATUS_ASSIGN);
                statusRecords.add(new BaseProjectSampleStatusRecord(projectSampleVO.getProjectId(), sampleGuid, sample.getStatus(), now));
            }
            int row = sampleDao.insertSelective(sample);
            if (row > 0) {
                if (assignmentList.size() > 0) {
                    assignmentDao.insertList(assignmentList);
                }
                recordDao.insertList(statusRecords);
                // ??????????????????
                BaseProject p = new BaseProject();
                p.setNumOfSample(row);
                p.setLastModifyUser(ThreadLocalManager.getUserId());
                p.setLastModifyTime(now);
                p.setId(projectSampleVO.getProjectId());
                projectDao.updateProjectAdd(p);
            }
            return row;
        } else {
            sample.setLastModifyTime(now);
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            return sampleDao.updateByPrimaryKeySelective(sample);
        }
    }

    /**
     * ????????????????????????
     *
     * @param sampleListVO
     * @return
     */
    public PageList<Page> getSampleList(SampleListVO sampleListVO) {
        sampleListVO.setUserId(ThreadLocalManager.getUserId());
        Page page = PageHelper.startPage(sampleListVO.getPageNum(), sampleListVO.getPageSize());
        if (!sampleListVO.getCheckRole().equals(AuthorizedConstants.ROLE_ADMIN)) {
            sampleDao.getSampleListByOther(sampleListVO);
        } else {
            sampleDao.getSampleList(sampleListVO);
        }
        return new PageList<>(page);
    }

    /**
     * ??????????????????
     *
     * @param deleteSampleVO
     * @return
     */
    public Integer deleteProjectSample(DeleteSampleVO deleteSampleVO) {
        // TODO ?????????
        // ????????????????????????????????????
        for (Integer id : deleteSampleVO.getSampleIds()) {
            Example example = new Example(BaseProjectSample.class);
            example.setTableName(ProjectConstants.getSampleTableName(deleteSampleVO.getProjectId()));
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id", id);
            BaseProjectSample sample = sampleDao.selectOneByExample(example);
            if (!sample.getStatus().equals(SampleConstants.STATUS_INIT)) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????????????????");
            }
        }
        int row = sampleDao.deleteSample(deleteSampleVO);
        if (row > 0) {
            // ??????????????????
            BaseProject p = new BaseProject();
            p.setNumOfSample(row);
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(new Date());
            p.setId(deleteSampleVO.getProjectId());
            projectDao.updateProjectDel(p);
        }
        return row;
    }

    /**
     * ??????????????????
     *
     * @param detailVO
     * @return
     */
    public ProjectSampleDTO getSampleDetail(ProjectSampleDetailVO detailVO) {
        BaseProjectSample sample = this.getSampleByGuid(detailVO.getProjectId(), detailVO.getSampleGuid());
        ProjectSampleDTO res = new ProjectSampleDTO();
        BeanUtils.copyProperties(sample, res);
        if (res.getStatus() > SampleConstants.STATUS_INIT) {
            List<SampleAssignNameDTO> nameDTOS = sampleDao.getSampleAssignName(detailVO.getProjectId(), sample.getSampleGuid());
            List<String> aids = new ArrayList<>();
            for (SampleAssignNameDTO name : nameDTOS) {
                if (name.getType().equals(SampleConstants.ASSIGN_TYPE_HEAD)) {
                    res.setManagerName(name.getUserName());
                } else {
                    aids.add(name.getUserName());
                }
            }
            res.setAssNames(aids);
        }
        if (ProjectConstants.OPEN.equals(projectService.getProjectConfig(detailVO.getProjectId()).getMoreSampleInfo())) {
            // ???????????????????????????
            SampleSearchMoreVO moreVO = new SampleSearchMoreVO();
            moreVO.setSampleGuid(sample.getSampleGuid());
            moreVO.setProjectId(detailVO.getProjectId());
            res.setAddressList(this.getSampleMoreAddress(moreVO, 1));
            res.setContactList(this.getSampleMoreContact(moreVO, 1));
            res.setTouchList(this.getSampleMoreTouch(moreVO, 1));
        }
        return res;
    }

    /**
     * ??????????????????
     *
     * @param detailVO
     * @return
     */
    public List<SampleAssignNameDTO> getSampleAssign(ProjectSampleDetailVO detailVO) {
        List<SampleAssignNameDTO> nameDTOS = sampleDao.getSampleAssignName(detailVO.getProjectId(), detailVO.getSampleGuid());
        return nameDTOS;
    }

    /**
     * ??????code????????????
     *
     * @param codeCheckVO
     * @return
     */
    public boolean checkSampleCodeExist(SampleCodeCheckVO codeCheckVO) {
        return sampleDao.checkSampleExistByCode(codeCheckVO.getProjectId(), codeCheckVO.getCode(), null) > 0;
    }

    /**
     * ????????????
     *
     * @param importVO
     * @return
     */
    public List<ProjectSampleVO> insertSampleByImport(SampleImportVO importVO) {
        Date now = new Date();
        // ??????????????????
        Example ex = new Example(BaseProjectSample.class);
        ex.setTableName(ProjectConstants.getSampleTableName(importVO.getProjectId()));
        int sampleCount = sampleDao.selectCountByExample(ex);

        List<ProjectSampleVO> res = new ArrayList<>();
        List<BaseProjectSample> insertList = new ArrayList<>();
        List<BaseProjectSampleStatusRecord> statusRecords = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        for (ProjectSampleVO sampleTemp : importVO.getSampleList()) {
            // ?????????????????????????????????????????????
            if (StringUtils.isEmpty(sampleTemp.getCode())
                    || codeList.contains(sampleTemp.getCode())
                    || sampleDao.checkSampleExistByCode(importVO.getProjectId(), sampleTemp.getCode(), null) > 0) {
                res.add(sampleTemp);
                continue;
            }
            codeList.add(sampleTemp.getCode());
            BaseProjectSample sample = new BaseProjectSample();
            BeanUtils.copyProperties(sampleTemp, sample);
            sample.setSampleGuid(UUIDUtil.getRandomUUID());
            sample.setIfVirtual(SampleConstants.VIRTUAL_NO);
            sample.setLastModifyTime(now);
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            sample.setCreateUser(ThreadLocalManager.getUserId());
            sample.setCreateTime(now);
            sample.setIsDelete(SampleConstants.INIT_VALUE);
            sample.setStatus(SampleConstants.STATUS_INIT);
            sample.setRandomNumber((int) ((Math.random() * 9 + 1) * 10000000));
            insertList.add(sample);
            statusRecords.add(new BaseProjectSampleStatusRecord(importVO.getProjectId(), sample.getSampleGuid(), sample.getStatus(), now));
        }
        if (insertList.size() > 0) {
            // ??????????????????
            Integer userRole = projectService.getProjectOwnerRole(importVO.getProjectId());
            if (userRole.equals(UserConstants.ROLE_COMMON) || userRole.equals(UserConstants.ROLE_VIP) || userRole.equals(UserConstants.ROLE_CONPANY)) {
                if (insertList.size() + sampleCount >= SampleConstants.TOP_LIMIT) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
                }
            }

            // ?????????????????????
            if (importVO.getCheckRole() == AuthorizedConstants.ROLE_INTERVIEWER) {
                List<BaseSampleAssignment> assignmentList = new ArrayList<>();
                for (BaseProjectSample sample : insertList) {
                    BaseSampleAssignment assignment = new BaseSampleAssignment();
                    assignment.setType(1);
                    assignment.setLastModifyUser(ThreadLocalManager.getUserId());
                    assignment.setLastModifyTime(now);
                    assignment.setCreateUser(ThreadLocalManager.getUserId());
                    assignment.setCreateTime(now);
                    assignment.setSampleGuid(sample.getSampleGuid());
                    assignment.setIsDelete(ProjectConstants.DELETE_NO);
                    assignmentList.add(assignment);
                    sample.setStatus(SampleConstants.STATUS_ASSIGN);
                    statusRecords.add(new BaseProjectSampleStatusRecord(importVO.getProjectId(), sample.getSampleGuid(), sample.getStatus(), now));
                }
                assignmentList.get(0).setDynamicTableName(ProjectConstants.getSampleAssignmentTableName(importVO.getProjectId()));
                assignmentDao.insertList(assignmentList);
            }
            insertList.get(0).setDynamicTableName(ProjectConstants.getSampleTableName(importVO.getProjectId()));
            sampleDao.insertList(insertList);
            recordDao.insertList(statusRecords);
            // ??????????????????
            BaseProject p = new BaseProject();
            p.setNumOfSample(insertList.size());
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(now);
            p.setId(importVO.getProjectId());
            projectDao.updateProjectAdd(p);
        }
        return res;
    }

    /**
     * ????????????
     *
     * @param importVO
     * @return
     */
    public List<ProjectSampleVO> updateSampleByImport(SampleImportVO importVO) {
        Date now = new Date();
        List<ProjectSampleVO> res = new ArrayList<>();
        List<BaseProjectSample> updateList = new ArrayList<>();
        for (ProjectSampleVO sampleTemp : importVO.getSampleList()) {
            // ????????????ID, ???????????????????????????????????????
            if (StringUtils.isEmpty(sampleTemp.getCode())) {
                res.add(sampleTemp);
                continue;
            }
            BaseProjectSample sample = new BaseProjectSample();
            BeanUtils.copyProperties(sampleTemp, sample);
            sample.setLastModifyTime(now);
            sample.setLastModifyUser(ThreadLocalManager.getUserId());
            updateList.add(sample);
        }
        if (updateList.size() > 0) {
            sampleDao.updateSampleListByCode(importVO.getProjectId(), updateList);
        }
        return res;
    }

    /**
     * ??????
     *
     * @param exportVO
     * @return
     * @throws Exception
     */
    public int exportSample(SampleExportVO exportVO) throws Exception {
        List<SampleListDTO> data = new ArrayList<>();
        if ("ALL".equals(exportVO.getOpt())) {
            SampleListVO searchVO = new SampleListVO();
            searchVO.setUserId(ThreadLocalManager.getUserId());
            searchVO.setCheckRole(exportVO.getCheckRole());
            searchVO.setProjectId(exportVO.getProjectId());
            data = sampleDao.getSampleList(searchVO);
        } else if ("SEARCH".equals(exportVO.getOpt())) {
            exportVO.getSearchVO().setUserId(ThreadLocalManager.getUserId());
            exportVO.getSearchVO().setCheckRole(exportVO.getCheckRole());
            exportVO.getSearchVO().setCheckRole(exportVO.getProjectId());
            data = sampleDao.getSampleList(exportVO.getSearchVO());
        } else {
            data = sampleDao.getSampleListByIds(exportVO.getProjectId(), exportVO.getSampleIds());
        }
        JSONArray array = JSON.parseArray(JSON.toJSONString(data));
        String pre = "/export/" + exportVO.getProjectId() + "/sample/";
        String path = filePath + pre;
        Map<String, Object> res = new HashMap<>();
        if ("EXCEL".equals(exportVO.getFileType())) {
            res = ExcelUtil.createExcelFile("SAMPLE", exportVO.getProperties(), array, path);
        } else if ("CSV".equals(exportVO.getFileType())) {
            res = CsvUtil.createCsvFile("SAMPLE", exportVO.getProperties(), array, path);
        } else if ("TXT".equals(exportVO.getFileType())) {
            res = TxtUtil.createTextFile("SAMPLE", exportVO.getProperties(), array, path);
        }
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(res.get("fileName").toString());
        exportHistory.setFileSize(Long.parseLong(res.get("fileSize").toString()));
        exportHistory.setFilePath(pre + res.get("fileName").toString());
        exportHistory.setType("SAMPLE");
        exportHistory.setFileType(exportVO.getFileType());
        exportHistory.setProjectId(exportVO.getProjectId());
        exportHistory.setDescription(exportVO.getDescription());
        exportHistory.setCreateUser(ThreadLocalManager.getUserId());
        exportHistory.setCreateTime(new Date());
        int row = exportDao.insert(exportHistory);
        if (row > 0) {
            // ?????????????????????
            BaseProject p = new BaseProject();
            p.setFileSize(Long.parseLong(res.get("fileSize").toString()));
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(new Date());
            p.setId(exportVO.getProjectId());
            projectDao.updateProjectAdd(p);
        }
        return row;
    }

    /**
     * ???????????????????????????
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getSampleDownList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        HistoryListVO listVO = new HistoryListVO();
        listVO.setProjectId(pageParam.getProjectId());
        listVO.setType("SAMPLE");
        List<ExportHistoryDTO> historyList = exportDao.getExportHistory(listVO);
        for (ExportHistoryDTO historyDTO : historyList) {
            historyDTO.setFileSizeStr(FileUtil.byteFormat(historyDTO.getFileSize(), true));
        }
        return new PageList<>(page);
    }

    /**
     * ??????????????????
     *
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadSample(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/sample/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * ????????????
     *
     * @param delVO
     * @return
     */
    public int deleteSampleFile(SampleExportDelVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/sample/";
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
     * ??????????????????????????? ????????????
     *
     * @param assignVO
     * @return
     */
    public List<BaseProjectSample> getSampleAssignList(SampleAssignVO assignVO) {
        Example example = new Example(BaseProjectSample.class);
        example.setTableName(ProjectConstants.getSampleTableName(assignVO.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", SampleConstants.STATUS_INIT);
        criteria.andEqualTo("ifVirtual", SampleConstants.VIRTUAL_NO);
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        if (!StringUtils.isEmpty(assignVO.getProperty()) && !StringUtils.isEmpty(assignVO.getKeyword())) {
            criteria.andLike(assignVO.getProperty(), "%" + assignVO.getKeyword() + "%");
        }
        if (null != assignVO.getCheckRole() && assignVO.getCheckRole() != AuthorizedConstants.ROLE_ADMIN) {
            criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        }
        if (!StringUtils.isEmpty(assignVO.getAuthCondition())) {
            criteria.andCondition(assignVO.getAuthCondition());
        }
        return sampleDao.selectByExample(example);

    }

    /**
     * ??????????????????
     *
     * @param assignTeamVO
     * @return
     */
    public Integer insertAssign(AssignTeamVO assignTeamVO) {
        Date now = new Date();
        List<BaseSampleAssignment> assignmentList = new ArrayList<>();
        List<BaseProjectSampleStatusRecord> statusRecords = new ArrayList<>();
        if (!"SELECT".equals(assignTeamVO.getOpt())) {
            List<String> sampleGuids = sampleDao.getAssignSampleGuids(assignTeamVO);
            assignTeamVO.setSampleGuids(sampleGuids);
        }
        // ??????????????????????????????????????????
        for (int i = 0; i < assignTeamVO.getSampleGuids().size(); i++) {
            if (assignTeamVO.getManagerId() != null) {
                BaseSampleAssignment sampleAssignment = new BaseSampleAssignment();
                sampleAssignment.setSampleGuid(assignTeamVO.getSampleGuids().get(i));
                sampleAssignment.setCreateUser(ThreadLocalManager.getUserId());
                sampleAssignment.setType(SampleConstants.ASSIGN_TYPE_HEAD);
                sampleAssignment.setCreateTime(now);
                sampleAssignment.setIsDelete(ProjectConstants.DELETE_NO);
                sampleAssignment.setTeamUserId(assignTeamVO.getManagerId());
                sampleAssignment.setLastModifyUser(ThreadLocalManager.getUserId());
                sampleAssignment.setLastModifyTime(now);
                assignmentList.add(sampleAssignment);
            }
            // ???????????????????????????????????????
            if (assignTeamVO.getAssistantId() != null) {
                for (int j = 0; j < assignTeamVO.getAssistantId().size(); j++) {
                    BaseSampleAssignment assignment = new BaseSampleAssignment();
                    assignment.setSampleGuid(assignTeamVO.getSampleGuids().get(i));
                    assignment.setIsDelete(ProjectConstants.DELETE_NO);
                    assignment.setCreateUser(ThreadLocalManager.getUserId());
                    assignment.setType(SampleConstants.ASSIGN_TYPE_AID);
                    assignment.setCreateTime(now);
                    assignment.setTeamUserId(assignTeamVO.getAssistantId().get(j));
                    assignment.setLastModifyUser(ThreadLocalManager.getUserId());
                    assignment.setLastModifyTime(now);
                    assignmentList.add(assignment);
                }
            }
            statusRecords.add(new BaseProjectSampleStatusRecord(assignTeamVO.getProjectId(), assignTeamVO.getSampleGuids().get(i), SampleConstants.STATUS_ASSIGN, now));
        }
        int row = 0;
        if (null != assignmentList && !assignmentList.isEmpty()) {
            assignmentList.get(0).setDynamicTableName(ProjectConstants.getSampleAssignmentTableName(assignTeamVO.getProjectId()));
            row = assignmentDao.insertList(assignmentList);
        }
        if (row > 0) {
            SampleUpdateVO updateVO = new SampleUpdateVO();
            updateVO.setProjectId(assignTeamVO.getProjectId());
            updateVO.setSampleGuids(assignTeamVO.getSampleGuids());
            updateVO.setStatus(SampleConstants.STATUS_ASSIGN);
            // ??????????????????????????????
            sampleDao.updateSamplesStatus(updateVO);
            recordDao.insertList(statusRecords);
        }
        return row;
    }

    /**
     * ????????????
     *
     * @param assignTeamVO
     * @return
     */
    public Integer updateAssign(AssignTeamVO assignTeamVO) {
        List<BaseSampleAssignment> assignmentList = new ArrayList<>();
        BaseSampleAssignment assignment = new BaseSampleAssignment();
        assignment.setType(SampleConstants.ASSIGN_TYPE_AID);
        assignment.setDynamicTableName(ProjectConstants.getSampleAssignmentTableName(assignTeamVO.getProjectId()));
        assignment.setSampleGuid(assignTeamVO.getSampleGuids().get(0));
        assignmentDao.delete(assignment);
        for (Integer userId : assignTeamVO.getAssistantId()) {
            BaseSampleAssignment sampleAssign = new BaseSampleAssignment();
            sampleAssign.setSampleGuid(assignTeamVO.getSampleGuids().get(0));
            sampleAssign.setIsDelete(ProjectConstants.DELETE_NO);
            sampleAssign.setCreateUser(ThreadLocalManager.getUserId());
            sampleAssign.setType(SampleConstants.ASSIGN_TYPE_AID);
            sampleAssign.setCreateTime(new Date());
            sampleAssign.setTeamUserId(userId);
            sampleAssign.setLastModifyUser(ThreadLocalManager.getUserId());
            sampleAssign.setLastModifyTime(new Date());
            assignmentList.add(sampleAssign);
        }
        int row = 0;
        if (!assignmentList.isEmpty()) {
            assignmentList.get(0).setDynamicTableName(ProjectConstants.getSampleAssignmentTableName(assignTeamVO.getProjectId()));
            row = assignmentDao.insertList(assignmentList);
        }
        return row;
    }

    /**
     * ??????????????????
     *
     * @param assignListVO
     * @return
     */
    public Integer insertBatchAssign(SampleImportAssignListVO assignListVO) {
        Date now = new Date();
        List<BaseSampleAssignment> assignmentList = new ArrayList<>();
        List<String> sampleGuids = new ArrayList<>();
        List<BaseProjectSampleStatusRecord> statusRecords = new ArrayList<>();
        for (SampleImportAssignVO assignVO : assignListVO.getSampleAssigns()) {
            BaseProjectSample sample = sampleDao.getSampleGuidByCode(assignListVO.getProjectId(), assignVO.getCode());
            if (StringUtils.isEmpty(sample) || !sample.getStatus().equals(SampleConstants.STATUS_INIT)) continue;
            Integer mUserId = teamUserDao.getTeamUserId(assignListVO.getProjectId(), assignVO.getUserName());
            if (mUserId == null) continue;
            sampleGuids.add(sample.getSampleGuid());
            BaseSampleAssignment sampleAssignment = new BaseSampleAssignment();
            sampleAssignment.setSampleGuid(sample.getSampleGuid());
            sampleAssignment.setIsDelete(ProjectConstants.DELETE_NO);
            sampleAssignment.setCreateUser(ThreadLocalManager.getUserId());
            sampleAssignment.setType(SampleConstants.ASSIGN_TYPE_HEAD);
            sampleAssignment.setCreateTime(now);
            sampleAssignment.setTeamUserId(mUserId);
            sampleAssignment.setLastModifyUser(ThreadLocalManager.getUserId());
            sampleAssignment.setLastModifyTime(now);
            assignmentList.add(sampleAssignment);
            if (!StringUtils.isEmpty(assignVO.getAidNames())) {
                String[] aidNames = assignVO.getAidNames().split("???");
                for (String name : aidNames) {
                    Integer userId = teamUserDao.getTeamUserId(assignListVO.getProjectId(), name);
                    if (userId == null) continue;
                    BaseSampleAssignment assignment = new BaseSampleAssignment();
                    assignment.setSampleGuid(sample.getSampleGuid());
                    assignment.setIsDelete(ProjectConstants.DELETE_NO);
                    assignment.setCreateUser(ThreadLocalManager.getUserId());
                    assignment.setType(SampleConstants.ASSIGN_TYPE_AID);
                    assignment.setCreateTime(now);
                    assignment.setTeamUserId(userId);
                    assignment.setLastModifyUser(ThreadLocalManager.getUserId());
                    assignment.setLastModifyTime(now);
                    assignmentList.add(assignment);
                }
            }
            statusRecords.add(new BaseProjectSampleStatusRecord(assignListVO.getProjectId(), sample.getSampleGuid(), SampleConstants.STATUS_ASSIGN, now));
        }
        int row = 0;
        if (assignmentList.size() > 0) {
            assignmentList.get(0).setDynamicTableName(ProjectConstants.getSampleAssignmentTableName(assignListVO.getProjectId()));
            row = assignmentDao.insertList(assignmentList);
            if (row > 0) {
                SampleUpdateVO updateVO = new SampleUpdateVO();
                updateVO.setProjectId(assignListVO.getProjectId());
                updateVO.setSampleGuids(sampleGuids);
                updateVO.setStatus(SampleConstants.STATUS_ASSIGN);
                // ??????????????????????????????
                sampleDao.updateSamplesStatus(updateVO);
                recordDao.insertList(statusRecords);
            }
        }
        return row;
    }

    /**
     * ????????????????????????
     *
     * @param teamMemberVO
     * @return
     */
    public List<TeamMemberDTO> getTeamMember(TeamMemberSearchVO teamMemberVO) {
        teamMemberVO.setUserId(ThreadLocalManager.getUserId());
        return teamUserDao.getTeamMemberList(teamMemberVO);
    }

    /**
     * ??????/??????????????????
     *
     * @param propertyVO
     * @return
     */
    public Integer saveProjectProperty(ProjectPropertyVO propertyVO) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        BeanUtils.copyProperties(propertyVO, projectProperty);
        if (propertyVO.getId() == null) {
            projectProperty.setCreateUser(ThreadLocalManager.getUserId());
            projectProperty.setCreateTime(new Date());
            propertyDao.insertSelective(projectProperty);
        } else {
            projectProperty.setCreateUser(ThreadLocalManager.getUserId());
            projectProperty.setCreateTime(new Date());
            propertyDao.updateByPrimaryKeySelective(projectProperty);
        }
        return projectProperty.getId();
    }

    /**
     * ????????????????????????
     *
     * @param projectId
     * @return
     */
    public BaseProjectProperty getProjectSampleProperty(Integer projectId) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setProjectId(projectId);
        return propertyDao.selectOne(projectProperty);
    }

    /**
     * @param projectProperty
     * @return
     */
    public HashSet<String> getSamplePropertySet(BaseProjectProperty projectProperty) {
        String useProperty = projectProperty.getUseProperty();
        HashSet<String> sampleColumns = new HashSet<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(useProperty)) {
            JSONArray jsonArray = JSONArray.parseArray(useProperty);
            for (int i = 0; i < jsonArray.size(); i++) {
                sampleColumns.add(CommonProperty.sampleProperty2Field.get(jsonArray.getString(i)));
            }
        }
        return sampleColumns;
    }

    /**
     * ????????????????????????
     *
     * @param samplePropertySaveVO
     * @return
     */
    public Integer saveProjectPropertyAlisa(SamplePropertySaveVO samplePropertySaveVO) {
        BaseProjectAllProperty allProperty = new BaseProjectAllProperty();
        BeanUtils.copyProperties(samplePropertySaveVO, allProperty);
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setId(samplePropertySaveVO.getId());
        projectProperty.setAllProperty(JSON.toJSONString(allProperty));
        return propertyDao.updateByPrimaryKeySelective(projectProperty);
    }

    /**
     * ????????????????????????
     *
     * @param projectId
     * @return
     */
    public SamplePropertyDTO getSampleProperty(Integer projectId) {
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setProjectId(projectId);
        projectProperty = propertyDao.selectOne(projectProperty);
        SamplePropertyDTO propertyDTO = new SamplePropertyDTO();
        if (projectProperty != null) {
            propertyDTO.setListProperty(projectProperty.getListProperty());
            propertyDTO.setUseProperty(projectProperty.getUseProperty());
        }
        return propertyDTO;
    }

    /**
     * ????????????????????????
     *
     * @param templateVO
     * @return
     */
    public Integer savePropertyTemplate(PropertyTemplateVO templateVO) {
        BaseProjectPropertyTemplate propertyTemplate = new BaseProjectPropertyTemplate();
        propertyTemplate.setName(templateVO.getName());
//        BaseProjectAllProperty allProperty = new BaseProjectAllProperty();
        BaseProjectProperty projectProperty = new BaseProjectProperty();
        projectProperty.setProjectId(templateVO.getProjectId());
        projectProperty = propertyDao.selectOne(projectProperty);
        propertyTemplate.setAllProperty(projectProperty.getAllProperty());
        propertyTemplate.setUseProperty(templateVO.getUseProperty());
        propertyTemplate.setListProperty(templateVO.getListProperty());
        propertyTemplate.setMarkProperty(templateVO.getMarkProperty());
        propertyTemplate.setCreateUser(ThreadLocalManager.getUserId());
        propertyTemplate.setCreateTime(new Date());
        return templateDao.insert(propertyTemplate);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public List<BaseProjectPropertyTemplate> getPropertyTemplate() {
        Example example = new Example(BaseProjectPropertyTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        return templateDao.selectByExample(example);
    }

    /**
     * ?????? ?????? ??????????????????
     *
     * @param outDataSaveVO
     * @return
     */
    public int updateSampleOutData(SampleOutDataSaveVO outDataSaveVO) {
        Date now = new Date();
        int row = 0;
        if (outDataSaveVO.getOutDatas().size() > 0) {
            List<BaseProjectSample> res = new ArrayList<>();
            for (SampleOutDataVO dataVO : outDataSaveVO.getOutDatas()) {
                BaseProjectSample sample = new BaseProjectSample();
                sample.setSampleGuid(dataVO.getSampleGuid());
                BaseProjectSampleOutData outData = new BaseProjectSampleOutData();
                BeanUtils.copyProperties(dataVO, outData);
                sample.setExtraData(JSON.toJSONString(outData));
                sample.setLastModifyUser(ThreadLocalManager.getUserId());
                sample.setLastModifyTime(now);
                res.add(sample);
            }
            if (null != res && !res.isEmpty()) {
                row = sampleDao.updateSampleList(outDataSaveVO.getProjectId(), res);
            }
        }
        return row;
    }

    /**
     * ?????? ??????????????????
     *
     * @param outDataSaveVO
     * @return
     */
    public int updateSampleOutDataImport(SampleOutDataSaveVO outDataSaveVO) {
        Date now = new Date();
        int row = 0;
        if (outDataSaveVO.getOutDatas().size() > 0) {
            List<BaseProjectSample> res = new ArrayList<>();
            for (SampleOutDataVO dataVO : outDataSaveVO.getOutDatas()) {
                BaseProjectSample sample = sampleDao.getSampleGuidByCode(outDataSaveVO.getProjectId(), dataVO.getCode());
                if (StringUtils.isEmpty(sample)) continue;
                BaseProjectSampleOutData outData = new BaseProjectSampleOutData();
                BeanUtils.copyProperties(dataVO, outData);
                sample.setExtraData(JSON.toJSONString(outData));
                sample.setLastModifyUser(ThreadLocalManager.getUserId());
                sample.setLastModifyTime(now);
                res.add(sample);
            }
            if (null != res && !res.isEmpty()) {
                row = sampleDao.updateSampleList(outDataSaveVO.getProjectId(), res);
            }
        }
        return row;
    }

    /**
     * ??????????????????
     *
     * @param searchVO
     * @return
     */
    public BaseProjectSampleOutData getSampleOutData(SampleSearchMoreVO searchVO) {
        BaseProjectSample sample = this.getSampleByGuid(searchVO.getProjectId(), searchVO.getSampleGuid());
        return JSONObject.parseObject(sample.getExtraData(), BaseProjectSampleOutData.class);
    }

    /**
     * ?????? ?????? ??????????????????
     *
     * @param addressListVO
     * @return
     */
    public int saveSampleMoreAddress(SampleMoreAddressListVO addressListVO) {
        Date now = new Date();
        int row = 0;
        if (addressListVO.getAddressList().size() > 0) {
            List<BaseProjectSampleAddress> addressList = new ArrayList<>();
            for (SampleMoreAddressVO addressVO : addressListVO.getAddressList()) {
                BaseProjectSampleAddress address = new BaseProjectSampleAddress();
                BeanUtils.copyProperties(addressVO, address);
                address.setSampleGuid(addressListVO.getSampleGuid());
                address.setCreateTime(now);
                address.setCreateUser(ThreadLocalManager.getUserId());
                address.setLastModifyTime(now);
                address.setLastModifyUser(ThreadLocalManager.getUserId());
                address.setIsDelete(ProjectConstants.DELETE_NO);
                addressList.add(address);
            }
            addressList.get(0).setDynamicTableName(ProjectConstants.getSampleAddressTableName(addressListVO.getProjectId()));
            row = addressDao.insertList(addressList);
        }
        return row;
    }

    /**
     * ??????????????????????????????
     *
     * @param searchVO
     * @return
     */
    public List<BaseProjectSampleAddress> getSampleMoreAddress(SampleSearchMoreVO searchVO, Integer type) {
        Example example = new Example(BaseProjectSampleAddress.class);
        example.setTableName(ProjectConstants.getSampleAddressTableName(searchVO.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sampleGuid", searchVO.getSampleGuid());
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
//        if (type == 1) {
//            criteria.andCondition(" limit 5");
//        }
        return addressDao.selectByExample(example);
    }

    /**
     * ??????????????????????????????
     *
     * @param addressVO
     * @return
     */
    public int updateSampleMoreAddress(SampleMoreAddressVO addressVO) {
        BaseProjectSampleAddress address = new BaseProjectSampleAddress();
        BeanUtils.copyProperties(addressVO, address);
        address.setDynamicTableName(ProjectConstants.getSampleAddressTableName(addressVO.getProjectId()));
        address.setLastModifyTime(new Date());
        address.setLastModifyUser(ThreadLocalManager.getUserId());
        return addressDao.updateByPrimaryKeySelective(address);
    }

    /**
     * ??????????????????????????????
     *
     * @param delVO
     * @return
     */
    public int deleteSampleMoreAddress(SampleDelMoreVO delVO) {
        BaseProjectSampleAddress address = new BaseProjectSampleAddress();
        address.setDynamicTableName(ProjectConstants.getSampleAddressTableName(delVO.getProjectId()));
        address.setId(delVO.getId());
        address.setIsDelete(ProjectConstants.DELETE_YES);
        address.setDeleteUser(ThreadLocalManager.getUserId());
        address.setLastModifyTime(new Date());
        address.setLastModifyUser(ThreadLocalManager.getUserId());
        return addressDao.updateByPrimaryKeySelective(address);
    }

    /**
     * ?????? ?????? ??????????????????
     *
     * @param contactListVO
     * @return
     */
    public int saveSampleMoreContact(SampleMoreContactListVO contactListVO) {
        Date now = new Date();
        int row = 0;
        if (contactListVO.getContactList().size() > 0) {
            List<BaseProjectSampleContact> contactList = new ArrayList<>();
            for (SampleMoreContactVO contactVO : contactListVO.getContactList()) {
                BaseProjectSampleContact contact = new BaseProjectSampleContact();
                BeanUtils.copyProperties(contactVO, contact);
                contact.setSampleGuid(contactListVO.getSampleGuid());
                contact.setCreateTime(now);
                contact.setCreateUser(ThreadLocalManager.getUserId());
                contact.setLastModifyTime(now);
                contact.setLastModifyUser(ThreadLocalManager.getUserId());
                contact.setIsDelete(ProjectConstants.DELETE_NO);
                contactList.add(contact);
            }
            contactList.get(0).setDynamicTableName(ProjectConstants.getSampleContactsTableName(contactListVO.getProjectId()));
            row = contactDao.insertList(contactList);
        }
        return row;
    }

    /**
     * ??????????????????????????????
     *
     * @param searchVO
     * @return
     */
    public List<BaseProjectSampleContact> getSampleMoreContact(SampleSearchMoreVO searchVO, Integer type) {
        Example example = new Example(BaseProjectSampleContact.class);
        example.setTableName(ProjectConstants.getSampleContactsTableName(searchVO.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sampleGuid", searchVO.getSampleGuid());
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
//        if (type == 1) {
//            criteria.andCondition(" limit 5");
//        }
        return contactDao.selectByExample(example);
    }

    /**
     * ??????????????????????????????
     *
     * @param contactVO
     * @return
     */
    public int updateSampleMoreContact(SampleMoreContactVO contactVO) {
        BaseProjectSampleContact contact = new BaseProjectSampleContact();
        BeanUtils.copyProperties(contactVO, contact);
        contact.setDynamicTableName(ProjectConstants.getSampleContactsTableName(contactVO.getProjectId()));
        contact.setLastModifyTime(new Date());
        contact.setLastModifyUser(ThreadLocalManager.getUserId());
        return contactDao.updateByPrimaryKeySelective(contact);
    }

    /**
     * ??????????????????????????????
     *
     * @param delVO
     * @return
     */
    public int deleteSampleMoreContact(SampleDelMoreVO delVO) {
        BaseProjectSampleContact contact = new BaseProjectSampleContact();
        contact.setDynamicTableName(ProjectConstants.getSampleContactsTableName(delVO.getProjectId()));
        contact.setId(delVO.getId());
        contact.setIsDelete(ProjectConstants.DELETE_YES);
        contact.setDeleteUser(ThreadLocalManager.getUserId());
        contact.setLastModifyTime(new Date());
        contact.setLastModifyUser(ThreadLocalManager.getUserId());
        return contactDao.updateByPrimaryKeySelective(contact);
    }

    /**
     * ??????????????????????????????
     *
     * @param searchVO
     * @return
     */
    public List<BaseProjectSampleTouch> getSampleMoreTouch(SampleSearchMoreVO searchVO, Integer type) {
        Example example = new Example(BaseProjectSampleTouch.class);
        example.setTableName(ProjectConstants.getSampleTouchTableName(searchVO.getProjectId()));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sampleGuid", searchVO.getSampleGuid());
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
//        if (type == 1) {
//            criteria.andCondition(" limit 5");
//        }
        return touchDao.selectByExample(example);
    }

    /**
     * ??????????????????????????????
     *
     * @param touchVO
     * @return
     */
    public int saveSampleMoreTouch(SampleMoreTouchVO touchVO) {
        Date now = new Date();
        BaseProjectSampleTouch touch = new BaseProjectSampleTouch();
        BeanUtils.copyProperties(touchVO, touch);
        touch.setDynamicTableName(ProjectConstants.getSampleTouchTableName(touchVO.getProjectId()));
        touch.setLastModifyTime(now);
        touch.setLastModifyUser(ThreadLocalManager.getUserId());
        if (touch.getId() == null) {
            touch.setCreateTime(now);
            touch.setCreateUser(ThreadLocalManager.getUserId());
            touch.setIsDelete(ProjectConstants.DELETE_NO);
            return touchDao.insertSelective(touch);
        } else {
            return touchDao.updateByPrimaryKeySelective(touch);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param delVO
     * @return
     */
    public int deleteSampleMoreTouch(SampleDelMoreVO delVO) {
        BaseProjectSampleTouch touch = new BaseProjectSampleTouch();
        touch.setDynamicTableName(ProjectConstants.getSampleTouchTableName(delVO.getProjectId()));
        touch.setId(delVO.getId());
        touch.setIsDelete(ProjectConstants.DELETE_YES);
        touch.setDeleteUser(ThreadLocalManager.getUserId());
        touch.setLastModifyTime(new Date());
        touch.setLastModifyUser(ThreadLocalManager.getUserId());
        return touchDao.updateByPrimaryKeySelective(touch);
    }

    /**
     * ????????????
     *
     * @param recycleVO
     * @return
     */
    public int deleteSampleByRecycle(SampleRecycleVO recycleVO) {
        Date now = new Date();
        Integer projectId = recycleVO.getProjectId();
        BaseProject project = projectDao.selectByPrimaryKey(projectId);
        // ???????????????????????????
        // ????????? ?????? ?????? ????????????/???????????? ????????? ????????????
        Integer[] statusArr = {SampleConstants.STATUS_ASSIGN, SampleConstants.STATUS_REFUSED, SampleConstants.STATUS_YUYUE,
                SampleConstants.STATUS_INVALID, SampleConstants.STATUS_CALLING, SampleConstants.STATUS_NOONE};
        List<String> sampleGuids = new ArrayList<>();
        List<HashMap<String, Object>> recycleList = new ArrayList<>();
        SampleOptionVO sampleOptionVO = new SampleOptionVO();
        sampleOptionVO.setProjectId(projectId);
        sampleOptionVO.setStatusList(Arrays.asList(statusArr));
        sampleOptionVO.setUserId(ThreadLocalManager.getUserId());
        if ("ALL".equals(recycleVO.getType())) {
            recycleList = sampleDao.getSampleMapListByStatusUserId(sampleOptionVO);
        } else {
            if (null == recycleVO.getSampleIds() || recycleVO.getSampleIds().isEmpty()) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "???????????????");
            }
            sampleOptionVO.setSampleIds(recycleVO.getSampleIds());
            recycleList = sampleDao.getSampleMapListByIdsUserId(sampleOptionVO);
        }
        if (null == recycleList || recycleList.isEmpty()) {
            throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????????????????");
        }
        List<BaseProjectSampleStatusRecord> statusRecordList = new ArrayList<>();
        for (int i = 0; i < recycleList.size(); i++) {
            HashMap<String, Object> sampleMap = recycleList.get(i);
            String guid = sampleMap.get("sample_guid").toString();
            sampleGuids.add(guid);
            BaseProjectSampleStatusRecord statusRecord = new BaseProjectSampleStatusRecord(projectId, guid,
                    SampleConstants.STATUS_INIT, new Date());
            statusRecordList.add(statusRecord);
        }
        // ????????????
        SampleUpdateVO sampleUpdateVO = new SampleUpdateVO();
        sampleUpdateVO.setProjectId(projectId);
        sampleUpdateVO.setStatus(SampleConstants.STATUS_INIT);
        sampleUpdateVO.setSampleGuids(sampleGuids);
        int row = sampleDao.updateSamplesStatus(sampleUpdateVO);

        // ?????????????????????
        // int delNum = responseDao.deleteBySampleGuids(projectId, sampleGuids, ThreadLocalManager.getUserId());
        int delNum = 0;// ????????????????????????
        long d = 0l;
        Example example = new Example(BaseResponse.class);
        example.setTableName(ProjectConstants.getResponseTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        criteria.andIn("sampleGuid", sampleGuids);
        List<BaseResponse> responseList = responseDao.selectByExample(example);
        if (null != responseList && !responseList.isEmpty()) {
            for (BaseResponse response : responseList) {
                if (response.getResponseType() == ResponseConstants.RESPONSE_TYPE_VALID) {
                    delNum++;
                }
                d += response.getResponseDuration();
                response.setDynamicTableName(ProjectConstants.getResponseTableName(projectId));
                response.setDeleteTime(now);
                response.setDeleteUser(ThreadLocalManager.getUserId());
                response.setIsDelete(ProjectConstants.DELETE_YES);
                responseDao.updateByPrimaryKeySelective(response);
            }
        }
        // ???????????????????????????????????????
        project.setNumOfAnswer(project.getNumOfAnswer() - delNum);
        project.setAnswerTimeLen(project.getAnswerTimeLen() - d);
        projectDao.updateByPrimaryKeySelective(project);

        // ??????????????????
        assignmentDao.deleteBySampleGuids(projectId, sampleGuids, ThreadLocalManager.getUserId());

        // ????????????????????????
        statusRecordDao.insertList(statusRecordList);
        return row;
    }

    /**
     * ?????????????????????
     *
     * @param clearInitialVO
     * @return
     */
    public int deleteSampleByTouch(ClearInitialVO clearInitialVO) {
        clearInitialVO.setUserId(ThreadLocalManager.getUserId());
        // ??????????????????????????????
        int row = sampleDao.clearInitial(clearInitialVO);
        // ??????????????????
        BaseProject project = projectDao.selectByPrimaryKey(clearInitialVO.getProjectId());
        project.setNumOfSample(project.getNumOfSample() - row);
        projectDao.updateByPrimaryKeySelective(project);
        return row;
    }

    /**
     * ??????????????????
     *
     * @param repeatVO
     * @return
     */
    public int deleteSampleByRepeat(SampleRepeatVO repeatVO) {
        // ???????????????????????????
        String groupBy = "";
        for (String field : repeatVO.getFields()) {
            groupBy += CommonProperty.sampleProperty2Field.get(field);
            groupBy += ",";
        }
        groupBy = groupBy.substring(0, groupBy.length() - 1);
        repeatVO.setGroupBy(groupBy);
        repeatVO.setUserId(ThreadLocalManager.getUserId());
        int row = sampleDao.deleteDuplicate(repeatVO);
        // ??????????????????
        BaseProject project = projectDao.selectByPrimaryKey(repeatVO.getProjectId());
        project.setNumOfSample(project.getNumOfSample() - row);
        projectDao.updateByPrimaryKeySelective(project);
        return row;
    }

    /**
     * guid??????????????????
     *
     * @param projectId
     * @param sampleGuid
     * @return
     */
    public BaseProjectSample getSampleByGuid(Integer projectId, String sampleGuid) {
        Example example = new Example(BaseProjectSample.class);
        example.setTableName(ProjectConstants.getSampleTableName(projectId));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sampleGuid", sampleGuid);
        criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
        return sampleDao.selectOneByExample(example);
    }

    public HashMap<String, Object> getSampleMapByGuid(Integer projectId, String sampleGuid) {
        return sampleDao.getSampleMapByGuid(projectId, sampleGuid);
    }

    public HashMap<String, Object> getSampleMapById(Integer projectId, Integer sampleId) {
        return sampleDao.getSampleMapById(projectId, sampleId);
    }


    /**
     * ????????????????????????
     *
     * @param pageParam
     * @return
     */
    public PageList<Page> getNumberRuleList(PageParam pageParam) {
        Date now = new Date();
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<NumberRuleListDTO> res = numberRuleDao.getNumberRuleList(pageParam.getProjectId());
        if (null != res && !res.isEmpty()) {
            for (NumberRuleListDTO dto : res) {
                if (dto.getCreateUser().equals(ThreadLocalManager.getUserId())) {
                    dto.setCreateUserStr("???");
                }
                long ct = DateUtil.getDateDuration(dto.getCreateTime(), now);
                if (ct < 7 * 24 * 60 * 60L) {
                    dto.setCreateTimeStr(DateUtil.secondToHourChineseStrByLong(ct));
                }
                long ut = DateUtil.getDateDuration(dto.getUpdateTime(), now);
                if (ut < 7 * 24 * 60 * 60L) {
                    dto.setUpdateTimeStr(DateUtil.secondToHourChineseStrByLong(ut));
                }
            }
        }
        return new PageList<>(page);
    }

    /**
     * ????????????????????????
     *
     * @param ruleId
     * @return
     */
    public BaseNumberRule getNumberRule(Integer ruleId) {
        return numberRuleDao.selectByPrimaryKey(ruleId);
    }

    /**
     * ??????????????????
     *
     * @param ruleSaveVO
     * @return
     */
    public int saveNumberRule(NumberRuleSaveVO ruleSaveVO) {
        Date now = new Date();
        BaseNumberRule numberRule = new BaseNumberRule();
        BeanUtils.copyProperties(ruleSaveVO, numberRule);
        int row = 0;
        if (null == ruleSaveVO.getId()) {
            if (numberRuleDao.checkRuleExistByName(ruleSaveVO.getProjectId(), ruleSaveVO.getName(), null) > 0) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????????????????");
            }
            numberRule.setCreateTime(now);
            numberRule.setCreateUser(ThreadLocalManager.getUserId());
            numberRule.setLastModifyTime(now);
            numberRule.setLastModifyUser(ThreadLocalManager.getUserId());
            numberRule.setIsDelete(ProjectConstants.DELETE_NO);
            row = numberRuleDao.insertSelective(numberRule);
        } else {
            numberRule = numberRuleDao.selectByPrimaryKey(ruleSaveVO.getId());
            if (numberRuleDao.checkRuleExistByName(ruleSaveVO.getProjectId(), ruleSaveVO.getName(), numberRule.getName()) > 0) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "??????????????????????????????");
            }
            BeanUtils.copyProperties(ruleSaveVO, numberRule);
            numberRule.setLastModifyTime(now);
            numberRule.setLastModifyUser(ThreadLocalManager.getUserId());
            row = numberRuleDao.updateByPrimaryKeySelective(numberRule);
        }

        return row;
    }

    /**
     * ????????????????????????
     *
     * @param ruleId
     * @return
     */
    public int deleteNumberRule(Integer ruleId) {
        BaseNumberRule numberRule = numberRuleDao.selectByPrimaryKey(ruleId);
        numberRule.setIsDelete(ProjectConstants.DELETE_YES);
        numberRule.setDeleteTime(new Date());
        numberRule.setDeleteUser(ThreadLocalManager.getUserId());
        return numberRuleDao.updateByPrimaryKeySelective(numberRule);
    }

    /**
     * ?????????????????????
     *
     * @param projectId
     * @param sampleGuids
     */
    public int deleteFromPool(Integer projectId, List<String> sampleGuids) {
        String tableName = ProjectConstants.getSamplePoolTableName(projectId);
        boolean poolFlag = dataBaseService.validateTableExist(tableName);
        int row = 0;
        if (poolFlag) {
            Example example = new Example(BaseSamplePool.class);
            example.setTableName(tableName);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("sampleGuid", sampleGuids);
            criteria.andEqualTo("isDelete", ProjectConstants.DELETE_NO);
            row = samplePoolDao.deleteByExample(example);
        }
        return row;
    }

    /**
     * ????????????????????????
     *
     * @param templateDeleteVO
     * @return
     */
    public Integer deletePropertyTemplate(TemplateDeleteVO templateDeleteVO) {
        Example example = new Example(BaseProjectPropertyTemplate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", templateDeleteVO.getTemplateId());
        criteria.andEqualTo("createUser", ThreadLocalManager.getUserId());
        return templateDao.deleteByExample(example);
    }

    /**
     * ????????????
     *
     * @param disabledVO
     * @return
     */
    public Integer disableSample(SampleDisabledVO disabledVO) {
        // ????????????
        List<String> sampleGuids = new ArrayList<>();
        sampleGuids.add(disabledVO.getSampleGuid());
        int row = assignmentDao.deleteBySampleGuids(disabledVO.getProjectId(), sampleGuids, ThreadLocalManager.getUserId());

        // ??????????????????
        if (row > 0) {
            SampleUpdateVO updateVO = new SampleUpdateVO();
            updateVO.setProjectId(disabledVO.getProjectId());
            updateVO.setSampleGuids(sampleGuids);
            updateVO.setStatus(SampleConstants.STATUS_DISABLED);
            row = sampleDao.updateSamplesStatus(updateVO);

            BaseProjectSampleStatusRecord statusRecord = new BaseProjectSampleStatusRecord(disabledVO.getProjectId(), disabledVO.getSampleGuid(),
                    SampleConstants.STATUS_DISABLED, new Date());
            statusRecordDao.insertSelective(statusRecord);
        }

        return row;
    }

    /**
     * ??????????????????
     *
     * @param statusSetVO
     * @return
     */
    public int setSampleStatus(SampleStatusSetVO statusSetVO) {
        SampleUpdateVO updateVO = new SampleUpdateVO();
        updateVO.setProjectId(statusSetVO.getProjectId());
        List<String> sampleGuids = new ArrayList<>();
        sampleGuids.add(statusSetVO.getSampleGuid());
        updateVO.setSampleGuids(sampleGuids);
        updateVO.setStatus(statusSetVO.getStatus());
        // TODO ????????????????????? statusSetVO.getNotes()
        int row = sampleDao.updateSamplesStatus(updateVO);
        if (row > 0) {
            // ????????????
            BaseProjectSampleStatusRecord statusRecord = new BaseProjectSampleStatusRecord(statusSetVO.getProjectId(), statusSetVO.getSampleGuid(),
                    statusSetVO.getStatus(), new Date());
            statusRecordDao.insertSelective(statusRecord);
        }
        return row;
    }
}
