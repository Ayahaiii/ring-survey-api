package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.qnaire.ProjectModuleGroupDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectModuleGroup;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProjectModuleGroupDao extends MyMapper<BaseProjectModuleGroup> {

    List<ProjectModuleGroupDTO> getProjectModuleGroupList(Integer projectId);

    /**
     * 新建关联项目问卷与分组
     *
     * @param projectId
     * @param moduleId
     * @param groupId
     * @return
     */
    int insertModule2Group(Integer projectId, Integer moduleId, Integer groupId);

    /**
     * 删除问卷与分组的关联
     *
     * @param projectId
     * @param groupId
     * @return
     */
    int deleteModule2Group(Integer projectId, Integer groupId);
}
