package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.sample.TeamMemberDTO;
import com.monetware.ringsurvey.business.pojo.dto.team.TeamGroupUserCountDTO;
import com.monetware.ringsurvey.business.pojo.dto.team.TeamGroupUserDTO;
import com.monetware.ringsurvey.business.pojo.dto.team.TeamUserDTO;
import com.monetware.ringsurvey.business.pojo.dto.team.TeamUserInfoDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserIdAndNameDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectTeamUser;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectTeamUserRole;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectTeamUserToGroup;
import com.monetware.ringsurvey.business.pojo.vo.sample.TeamMemberSearchVO;
import com.monetware.ringsurvey.business.pojo.vo.team.TeamGroupUserSearchVO;
import com.monetware.ringsurvey.business.pojo.vo.team.TeamUserInfoVO;
import com.monetware.ringsurvey.business.pojo.vo.team.TeamUserSearchVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:20
 */
@Mapper
@Repository
public interface ProjectTeamUserDao extends MyMapper<BaseProjectTeamUser> {

    List<TeamUserDTO> getTeamUserList(TeamUserSearchVO userSearchVO);

    List<TeamUserDTO> getTeamUserListByIds(@Param("projectId") Integer projectId, @Param("list") List<Integer> list);

    List<TeamGroupUserDTO> getTeamGroupUserList(TeamGroupUserSearchVO groupUserSearchVO);

    TeamUserInfoDTO getTeamUserInfo(TeamUserInfoVO teamUserInfoVO);

    List<BaseProjectTeamUserRole> getTeamUserRole(TeamUserInfoVO teamUserInfoVO);

    List<BaseProjectTeamUserToGroup> getTeamUserGroup(TeamUserInfoVO teamUserInfoVO);

    List<TeamGroupUserCountDTO> getTeamUserCount(@Param("projectId") Integer projectId);

    Integer checkTeamUser(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    BaseProjectTeamUser getTeamUser(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    Integer getTeamUserId(@Param("projectId") Integer projectId, @Param("userName") String userName);

    List<TeamMemberDTO> getTeamMemberList(TeamMemberSearchVO teamMemberVO);

    //============================================ lu Begin =======================================

    /**
    * @Author: lu
    * @Date: 2020/4/1  ??????1:21
    * @Description:?????????????????????????????????????????????id list
    **/
    List<Integer> getInterviewerIdList(@Param("projectId")Integer projectId,@Param("userId") Integer userId);

    /**
    * @Author: lu
    * @Date: 2020/4/1  ??????2:33
    * @Description:?????????????????????????????????????????????id???name list
    **/
    List<UserIdAndNameDTO> getUserIdAndName(@Param("projectId") Integer projectId,@Param("userId") Integer userId);
    //============================================ lu End =========================================

}
