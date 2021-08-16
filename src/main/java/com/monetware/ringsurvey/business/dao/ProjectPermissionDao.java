package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectPermission;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:03
 */
@Mapper
@Repository
public interface ProjectPermissionDao extends MyMapper<BaseProjectPermission> {

    List<String> getProjectPermissionForUser(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

}
