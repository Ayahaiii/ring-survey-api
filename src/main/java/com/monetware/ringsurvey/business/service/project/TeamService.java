package com.monetware.ringsurvey.business.service.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.monetware.ringsurvey.business.dao.*;
import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.constants.SampleConstants;
import com.monetware.ringsurvey.business.pojo.constants.UserConstants;
import com.monetware.ringsurvey.business.pojo.dto.history.ExportHistoryDTO;
import com.monetware.ringsurvey.business.pojo.dto.team.*;
import com.monetware.ringsurvey.business.pojo.po.*;
import com.monetware.ringsurvey.business.pojo.vo.history.HistoryListVO;
import com.monetware.ringsurvey.business.pojo.vo.team.*;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.base.PageList;
import com.monetware.ringsurvey.system.base.PageParam;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.file.CsvUtil;
import com.monetware.ringsurvey.system.util.file.ExcelUtil;
import com.monetware.ringsurvey.system.util.file.FileUtil;
import com.monetware.ringsurvey.system.util.file.TxtUtil;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class TeamService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ProjectTeamUserDao teamUserDao;

    @Autowired
    private ProjectTeamGroupDao teamGroupDao;

    @Autowired
    private ProjectTeamUserToGroupDao teamUserToGroupDao;

    @Autowired
    private ProjectTeamUserRoleDao teamUserRoleDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ProjectExportDao exportDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Value("${fileUrl.upload}")
    private String filePath;

    @Autowired
    private ProjectService projectService;

    /**
     * ??????????????????
     *
     * @return
     */
    public List<BaseRole> getRoleList() {
        List<Integer> userIds = new ArrayList<>();
        userIds.add(0); // ????????????
        userIds.add(ThreadLocalManager.getUserId());
        Example example = new Example(BaseRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("createUser", userIds);
        criteria.andNotEqualTo("id", AuthorizedConstants.ROLE_OWNER);
        return roleDao.selectByExample(example);
    }

    /**
     * ??????????????????
     *
     * @param teamUsers
     * @return
     */
    public List<Integer> saveTeamUser(TeamUserListVO teamUsers) {
        // ????????????????????????????????????
        Integer userRole = projectService.getProjectOwnerRole(teamUsers.getProjectId());
        // ????????????????????????
        Example ex = new Example(BaseProjectTeamUser.class);
        Example.Criteria c = ex.createCriteria();
        c.andEqualTo("projectId", teamUsers.getProjectId());
        int teamMemberNum = teamUserDao.selectCountByExample(ex);
        if (userRole.equals(UserConstants.ROLE_VIP)) {
            if (teamMemberNum >= ProjectConstants.TEAM_MEMBER_VIP) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
            }
        } else if (userRole.equals(UserConstants.ROLE_CONPANY)) {
            if (teamMemberNum >= ProjectConstants.TEAM_MEMBER_COMPANY) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
            }
        } else if (userRole.equals(UserConstants.ROLE_CUSTOM)) {
            if (teamMemberNum >= ProjectConstants.TEAM_MEMBER_ORG) {
                throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
            }
        }

        List<Integer> res = new ArrayList<>();
        int row = 0;
        for (TeamUserVO teamUserVO : teamUsers.getTeamUsers()) {
            if (teamUserVO.getId() == null) {
                // ???????????????????????????
                Example example = new Example(BaseProjectTeamUser.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("userId", teamUserVO.getUserId());
                criteria.andEqualTo("projectId", teamUserVO.getProjectId());
                BaseProjectTeamUser checkTeamUser = teamUserDao.selectOneByExample(example);
                if (checkTeamUser != null) {
                    res.add(teamUserVO.getUserId());
                    continue;
                }
                // ????????????
                Date now = new Date();
                BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
                teamUser.setProjectId(teamUserVO.getProjectId());
                teamUser.setUserId(teamUserVO.getUserId());
                teamUser.setUserName(teamUserVO.getName());
                teamUser.setName(teamUserVO.getName());
                teamUser.setTelephone(teamUserVO.getTelephone());
                teamUser.setEmail(teamUserVO.getEmail());
                teamUser.setSampleAuth(teamUserVO.getSampleAuth());
                teamUser.setAuthCondition(teamUserVO.getAuthCondition());
                teamUser.setAuthType(teamUserVO.getAuthType());
                teamUser.setAuthEndTime(teamUserVO.getAuthDate());
                teamUser.setApproveUser(ThreadLocalManager.getUserId());
                teamUser.setApproveTime(now);
                teamUser.setApplyTime(now);
                teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
                // ????????????
                if (teamUserVO.getRoles().size() > 0) {
                    teamUserRoleDao.insertList(teamUserVO.getRoles());
                }
                // ?????????
                if (teamUserVO.getGroups().size() > 0) {
                    teamUserToGroupDao.insertList(teamUserVO.getGroups());
                }
                row += teamUserDao.insert(teamUser);
            } else {
                BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
                teamUser.setId(teamUserVO.getId());
                teamUser.setUserName(teamUserVO.getName());
                teamUser.setTelephone(teamUserVO.getTelephone());
                teamUser.setEmail(teamUserVO.getEmail());
                teamUser.setSampleAuth(teamUserVO.getSampleAuth());
                teamUser.setAuthCondition(teamUserVO.getAuthCondition());
                teamUser.setAuthType(teamUserVO.getAuthType());
                teamUser.setAuthEndTime(teamUserVO.getAuthDate());
                // ????????????
                if (teamUserVO.getRoles().size() > 0) {
                    teamUserRoleDao.insertList(teamUserVO.getRoles());
                }
                // ????????????
                if (teamUserVO.getDelRoleIds().size() > 0) {
                    Example example = new Example(BaseProjectTeamUserRole.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("projectId", teamUserVO.getProjectId());
                    criteria.andEqualTo("userId", teamUserVO.getUserId());
                    criteria.andIn("roleId", teamUserVO.getDelRoleIds());
                    teamUserRoleDao.deleteByExample(example);
                }
                // ?????????
                if (teamUserVO.getGroups().size() > 0) {
                    teamUserToGroupDao.insertList(teamUserVO.getGroups());
                }
                // ?????????
                if (teamUserVO.getDelGroupIds().size() > 0) {
                    Example example = new Example(BaseProjectTeamUserToGroup.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("projectId", teamUserVO.getProjectId());
                    criteria.andEqualTo("userId", teamUserVO.getUserId());
                    criteria.andIn("groupId", teamUserVO.getDelGroupIds());
                    teamUserToGroupDao.deleteByExample(example);
                }
                // ??????redis??????????????????
                String authKey = "RS_AUTH_" + teamUserVO.getProjectId() + "_" + teamUserVO.getUserId();
                if (redisUtil.hasKey(authKey)) redisUtil.remove(authKey);
                String roleKey = "RS_ROLE_" + teamUserVO.getProjectId() + "_" + teamUserVO.getUserId();
                if (redisUtil.hasKey(roleKey)) redisUtil.remove(roleKey);
                teamUserDao.updateByPrimaryKeySelective(teamUser);
            }
        }
        if (row > 0) {
            // ??????????????????
            BaseProject project = new BaseProject();
            project.setNumOfTeam(row);
            project.setLastModifyUser(ThreadLocalManager.getUserId());
            project.setLastModifyTime(new Date());
            project.setId(teamUsers.getProjectId());
            projectDao.updateProjectAdd(project);
        }
        return res;
    }

    /**
     * ????????????????????????
     *
     * @param teamUserInfoVO
     * @return
     */
    public TeamUserInfoDTO getTeamUserInfo(TeamUserInfoVO teamUserInfoVO) {
        TeamUserInfoDTO res = teamUserDao.getTeamUserInfo(teamUserInfoVO);
        res.setGroups(teamUserDao.getTeamUserGroup(teamUserInfoVO));
        res.setRoles(teamUserDao.getTeamUserRole(teamUserInfoVO));
        return res;
    }

    /**
     * ??????????????????
     *
     * @param importVO
     * @return
     */
    public List<TeamUserImportInfoVO> insertTeamUserByImport(TeamUserImportVO importVO) {
        Date now = new Date();
        Integer userRole = projectService.getProjectOwnerRole(importVO.getProjectId());
        // ????????????????????????
        Example ex = new Example(BaseProjectTeamUser.class);
        Example.Criteria c = ex.createCriteria();
        c.andEqualTo("projectId", importVO.getProjectId());
        int teamMemberNum = teamUserDao.selectCountByExample(ex);

        List<TeamUserImportInfoVO> errorData = new ArrayList<>();
        int row = 0;
        for (TeamUserImportInfoVO infoVO : importVO.getUserInfos()) {
            // ????????????????????????
            if (StringUtils.isEmpty(infoVO.getEmail())
                    && StringUtils.isEmpty(infoVO.getTelephone())
                    && StringUtils.isEmpty(infoVO.getUserName())) {
                errorData.add(infoVO);
                continue;
            }
            // ????????????????????????
            Integer userId = userDao.checkUser(infoVO);
            if (userId == null) {
                errorData.add(infoVO);
                continue;
            }
            // ????????????????????????????????????
            Integer count = teamUserDao.checkTeamUser(importVO.getProjectId(), userId);
            if (count > 0) {
                errorData.add(infoVO);
                continue;
            }
            // ????????????????????????
            BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
            BeanUtils.copyProperties(infoVO, teamUser);
            teamUser.setName(teamUser.getUserName());
            teamUser.setUserId(userId);
            teamUser.setApplyTime(now);
            teamUser.setProjectId(importVO.getProjectId());
            if (infoVO.getAuthDate() == null) {
                teamUser.setAuthType(ProjectConstants.AUTH_TYPE_FOREVER);
            } else if (infoVO.getAuthDate().before(now)) {
                teamUser.setAuthType(ProjectConstants.AUTH_TYPE_TIME);
                teamUser.setAuthEndTime(infoVO.getAuthDate());
                teamUser.setStatus(ProjectConstants.USER_STATUS_DISABLE);
            } else {
                teamUser.setAuthType(ProjectConstants.AUTH_TYPE_TIME);
                teamUser.setAuthEndTime(infoVO.getAuthDate());
                teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
            }
            teamUser.setSampleAuth(ProjectConstants.SAMPLE_TYPE_ALL);
            teamUser.setApproveTime(now);
            teamUser.setApproveUser(ThreadLocalManager.getUserId());

            // ??????????????????????????????
            if (userRole.equals(UserConstants.ROLE_VIP)) {
                if (teamMemberNum + row >= ProjectConstants.TEAM_MEMBER_VIP) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
                }
            } else if (userRole.equals(UserConstants.ROLE_CONPANY)) {
                if (teamMemberNum + row >= ProjectConstants.TEAM_MEMBER_COMPANY) {
                    throw new ServiceException(ErrorCode.CUSTOM_MSG, "????????????????????????????????????");
                }
            }

            // ???????????????
            BaseProjectTeamUserRole teamUserRole = new BaseProjectTeamUserRole();
            teamUserRole.setProjectId(importVO.getProjectId());
            teamUserRole.setUserId(userId);
            teamUserRole.setRoleId(AuthorizedConstants.ROLE_INTERVIEWER);
            teamUserRoleDao.insert(teamUserRole);
            row += teamUserDao.insertSelective(teamUser);
        }
        if (row > 0) {
            // ??????????????????
            BaseProject p = new BaseProject();
            p.setNumOfTeam(row);
            p.setLastModifyUser(ThreadLocalManager.getUserId());
            p.setLastModifyTime(now);
            p.setId(importVO.getProjectId());
            projectDao.updateProjectAdd(p);
        }
        return errorData;
    }

    /**
     * ????????????
     *
     * @param teamUserDelVO
     */
    public void deleteTeamUser(TeamUserDelVO teamUserDelVO) {
        // ???????????????
        Example gExample = new Example(BaseProjectTeamUserToGroup.class);
        Example.Criteria gCriteria = gExample.createCriteria();
        gCriteria.andEqualTo("projectId", teamUserDelVO.getProjectId());
        gCriteria.andIn("userId", teamUserDelVO.getUserIds());
        teamUserToGroupDao.deleteByExample(gExample);
        // ??????????????????
        Example rExample = new Example(BaseProjectTeamUserRole.class);
        Example.Criteria rCriteria = rExample.createCriteria();
        rCriteria.andEqualTo("projectId", teamUserDelVO.getProjectId());
        rCriteria.andIn("userId", teamUserDelVO.getUserIds());
        teamUserRoleDao.deleteByExample(rExample);
        // ????????????
        Example uExample = new Example(BaseProjectTeamUser.class);
        Example.Criteria uCriteria = uExample.createCriteria();
        uCriteria.andEqualTo("projectId", teamUserDelVO.getProjectId());
        uCriteria.andIn("userId", teamUserDelVO.getUserIds());
        int row = teamUserDao.deleteByExample(uExample);
        if (row > 0) {
            // ??????????????????
            BaseProject project = new BaseProject();
            project.setNumOfTeam(row);
            project.setLastModifyUser(ThreadLocalManager.getUserId());
            project.setLastModifyTime(new Date());
            project.setId(teamUserDelVO.getProjectId());
            projectDao.updateProjectDel(project);
        }
    }

    /**
     * ????????????????????????
     *
     * @param teamUserSearchVO
     * @return
     */
    public PageList<Page> getTeamUserList(TeamUserSearchVO teamUserSearchVO) {
        Page page = PageHelper.startPage(teamUserSearchVO.getPageNum(), teamUserSearchVO.getPageSize());
        teamUserSearchVO.setUserId(ThreadLocalManager.getUserId());
        List<TeamUserDTO> res = teamUserDao.getTeamUserList(teamUserSearchVO);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (TeamUserDTO userDTO : res) {
            try {
                if (userDTO.getJoinDate() != null) userDTO.setJoinDate(sdf.format(sdf.parse(userDTO.getJoinDate())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new PageList<>(page);
    }

    /**
     * ????????????
     *
     * @param teamUserStatusVO
     * @return
     */
    public int updateTeamUserAgree(TeamUserStatusVO teamUserStatusVO) {
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        teamUser.setId(teamUserStatusVO.getId());
        teamUser.setApproveTime(new Date());
        teamUser.setApproveUser(ThreadLocalManager.getUserId());
        teamUser.setStatus(ProjectConstants.USER_STATUS_ENABLE);
        return teamUserDao.updateByPrimaryKeySelective(teamUser);
    }

    /**
     * ??????
     *
     * @param teamUserStatusVO
     * @return
     */
    public int updateTeamUserDisable(TeamUserStatusVO teamUserStatusVO) {
        BaseProjectTeamUser teamUser = new BaseProjectTeamUser();
        teamUser.setId(teamUserStatusVO.getId());
        teamUser.setStatus(ProjectConstants.USER_STATUS_DISABLE);
        return teamUserDao.updateByPrimaryKeySelective(teamUser);
    }

    /**
     * ????????????
     *
     * @param teamGroupVO
     * @return
     */
    public int saveTeamGroup(TeamGroupVO teamGroupVO) {
        BaseProjectTeamGroup teamGroup = new BaseProjectTeamGroup();
        BeanUtils.copyProperties(teamGroupVO, teamGroup);
        if (teamGroup.getId() == null) {
            teamGroup.setCreateUser(ThreadLocalManager.getUserId());
            teamGroup.setCreateTime(new Date());
            return teamGroupDao.insert(teamGroup);
        } else {
            return teamGroupDao.updateByPrimaryKeySelective(teamGroup);
        }
    }

    /**
     * ????????????????????????
     *
     * @param projectId
     * @return
     */
    public TeamGroupResDTO getTeamGroupList(Integer projectId) {
        TeamGroupResDTO teamGroupResDTO = new TeamGroupResDTO();
        BaseProjectTeamGroup teamGroup = new BaseProjectTeamGroup();
        teamGroup.setProjectId(projectId);
        List<BaseProjectTeamGroup> teamGroups = teamGroupDao.select(teamGroup);
        List<TeamGroupDTO> result = new ArrayList<>();
        BaseProjectTeamGroup parentGroup;
        for (int i = 0; i < teamGroups.size(); i++) {
            parentGroup = teamGroups.get(i);
            if (parentGroup.getParentId() == null) {
                TeamGroupDTO teamGroupDTO = new TeamGroupDTO();
                teamGroupDTO.setId(parentGroup.getId());
                teamGroupDTO.setLabel(parentGroup.getName());
                teamGroupDTO.setChildren(getTeamGroupTree(teamGroups, parentGroup.getId()));
                result.add(teamGroupDTO);
            }
        }
        teamGroupResDTO.setTreeGroup(result);
        List<TeamGroupUserCountDTO> countDTOS = teamUserDao.getTeamUserCount(projectId);
        Map<Integer, Integer> resultCount = new HashMap<>();
        for (int i = 0; i < countDTOS.size(); i++) {
            resultCount.put(countDTOS.get(i).getId(), countDTOS.get(i).getCount());
        }
        teamGroupResDTO.setTreeValue(resultCount);
        return teamGroupResDTO;
    }

    /**
     * ??????????????????
     *
     * @param teamGroups
     * @param parentId
     * @return
     */
    private List<TeamGroupDTO> getTeamGroupTree(List<BaseProjectTeamGroup> teamGroups, Integer parentId) {
        List<TeamGroupDTO> result = new ArrayList<>();
        BaseProjectTeamGroup childGroup;
        for (int i = 0; i < teamGroups.size(); i++) {
            childGroup = teamGroups.get(i);
            if (childGroup.getParentId() == parentId) {
                TeamGroupDTO teamGroupDTO = new TeamGroupDTO();
                teamGroupDTO.setId(childGroup.getId());
                teamGroupDTO.setLabel(childGroup.getName());
                teamGroupDTO.setChildren(getTeamGroupTree(teamGroups, childGroup.getId()));
                result.add(teamGroupDTO);
            }
        }
        return result;
    }

    /**
     * ????????????
     *
     * @param teamGroupDelVO
     * @return
     */
    public void deleteTeamGroup(TeamGroupDelVO teamGroupDelVO) {
        // ???????????????????????????
        Example example1 = new Example(BaseProjectTeamUserToGroup.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("groupId", teamGroupDelVO.getIds());
        teamUserToGroupDao.deleteByExample(example1);
        // ?????????
        Example example = new Example(BaseProjectTeamGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", teamGroupDelVO.getIds());
        teamGroupDao.deleteByExample(example);
    }

    /**
     * ???????????????????????????
     *
     * @param userToGroupVO
     * @return
     */
    public int insertTeamUserToGroup(TeamUserToGroupVO userToGroupVO) {
        return teamUserToGroupDao.insertList(userToGroupVO.getUserToGroups());
    }

    /**
     * ???????????????????????????
     *
     * @param teamGroupUserSearchVO
     * @return
     */
    public PageList<Page> getTeamGroupUserList(TeamGroupUserSearchVO teamGroupUserSearchVO) {
        Page page = PageHelper.startPage(teamGroupUserSearchVO.getPageNum(), teamGroupUserSearchVO.getPageSize());
        teamUserDao.getTeamGroupUserList(teamGroupUserSearchVO);
        return new PageList<>(page);
    }

    /**
     * ?????????????????????
     *
     * @param userToGroupVO
     * @return
     */
    public int deleteTeamUserToGroup(TeamUserToGroupVO userToGroupVO) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < userToGroupVO.getUserToGroups().size(); i++) {
            ids.add(userToGroupVO.getUserToGroups().get(i).getId());
        }
        Example example = new Example(BaseProjectTeamUserToGroup.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        return teamUserToGroupDao.deleteByExample(example);
    }

    /**
     * ??????
     *
     * @param exportVO
     * @return
     * @throws Exception
     */
    public int exportTeamUser(TeamUserExportVO exportVO) throws Exception {
        List<TeamUserDTO> data = new ArrayList<>();
        if ("ALL".equals(exportVO.getOpt())) {
            TeamUserSearchVO searchVO = new TeamUserSearchVO();
            searchVO.setUserId(ThreadLocalManager.getUserId());
            searchVO.setProjectId(exportVO.getProjectId());
            data = teamUserDao.getTeamUserList(searchVO);
        } else if ("SEARCH".equals(exportVO.getOpt())) {
            exportVO.getSearchVO().setUserId(ThreadLocalManager.getUserId());
            data = teamUserDao.getTeamUserList(exportVO.getSearchVO());
        } else {
            data = teamUserDao.getTeamUserListByIds(exportVO.getProjectId(), exportVO.getTeamUserIds());
        }
        JSONArray array = JSON.parseArray(JSON.toJSONString(data));
        String pre = "/export/" + exportVO.getProjectId() + "/team/";
        String path = filePath + pre;
        Map<String, Object> res = new HashMap<>();
        if ("EXCEL".equals(exportVO.getFileType())) {
            res = ExcelUtil.createExcelFile("TEAM", exportVO.getProperties(), array, path);
        } else if ("CSV".equals(exportVO.getFileType())) {
            res = CsvUtil.createCsvFile("TEAM", exportVO.getProperties(), array, path);
        } else if ("TXT".equals(exportVO.getFileType())) {
            res = TxtUtil.createTextFile("TEAM", exportVO.getProperties(), array, path);
        }
        BaseProjectExportHistory exportHistory = new BaseProjectExportHistory();
        exportHistory.setName(res.get("fileName").toString());
        exportHistory.setFileSize(Long.parseLong(res.get("fileSize").toString()));
        exportHistory.setFilePath(pre + res.get("fileName").toString());
        exportHistory.setType("TEAM");
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
    public PageList<Page> getTeamDownList(PageParam pageParam) {
        Page page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        HistoryListVO listVO = new HistoryListVO();
        listVO.setProjectId(pageParam.getProjectId());
        listVO.setType("TEAM");
        List<ExportHistoryDTO> historyList = exportDao.getExportHistory(listVO);
        for (ExportHistoryDTO historyDTO : historyList) {
            historyDTO.setFileSizeStr(FileUtil.byteFormat(historyDTO.getFileSize(), true));
        }
        return new PageList<>(page);
    }

    /**
     * ????????????????????????
     *
     * @param id
     * @param response
     * @throws Exception
     */
    public void downloadTeamUser(Integer id, HttpServletResponse response) throws Exception {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(id);
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/team/" + exportHistory.getName();
        FileUtil.downloadFileToClient(path, response);
    }

    /**
     * ????????????
     *
     * @param delVO
     * @return
     */
    public int deleteTeamUserFile(TeamUserExportDelVO delVO) {
        BaseProjectExportHistory exportHistory = exportDao.selectByPrimaryKey(delVO.getId());
        String path = filePath + "/export/" + exportHistory.getProjectId() + "/team/";
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

}
