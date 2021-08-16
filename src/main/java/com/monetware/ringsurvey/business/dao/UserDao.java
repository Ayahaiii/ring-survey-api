package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.user.UserSearchDTO;
import com.monetware.ringsurvey.business.pojo.dto.user.UserSelectDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseUser;
import com.monetware.ringsurvey.business.pojo.vo.team.TeamUserImportInfoVO;
import com.monetware.ringsurvey.business.pojo.vo.user.UserSearchVO;
import com.monetware.ringsurvey.business.pojo.vo.user.UserSelectVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:30
 */
@Mapper
@Repository
public interface UserDao extends MyMapper<BaseUser> {

    UserSearchDTO searchUser(UserSearchVO userSearchVO);

    Integer checkUser(TeamUserImportInfoVO importInfoVO);

    List<UserSelectDTO> getUserList(UserSelectVO userSelectVO);
}
