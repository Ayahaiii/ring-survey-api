package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectTeamUserRole;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Linked
 * @date 2020/2/17 18:28
 */
@Mapper
@Repository
public interface ProjectTeamUserRoleDao extends MyMapper<BaseProjectTeamUserRole> {

}
