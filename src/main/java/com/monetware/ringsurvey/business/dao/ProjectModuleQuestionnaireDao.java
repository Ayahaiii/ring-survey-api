package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.qnaire.*;
import com.monetware.ringsurvey.business.pojo.vo.qnaire.QnaireSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 项目问卷模块、项目问卷、项目问卷分组查询Dao
 */
@Mapper
@Repository
public interface ProjectModuleQuestionnaireDao {

    List<String> getUsedModuleCode(@Param("projectId") Integer projectId);

    /**
     * 多问卷查询列表
     *
     * @param projectId
     * @return
     */
    List<ProjectModuleQuestionnaireListDTO> getProjectModuleQuestionnaireList(@Param("projectId") Integer projectId);

    /**
     * 问卷模块查询通用列表
     *
     * @param projectId
     * @return
     */
    List<ProjectModuleQuestionListDTO> getProjectModuleQuestionList(@Param("projectId") Integer projectId);

    /**
     * 问卷历史查询列表
     *
     * @param searchVO
     * @return
     */
    List<ProjectModuleHistoryDTO> getProjectModuleHistoryList(QnaireSearchVO searchVO);

    /**
     * 问卷选择下拉列表
     *
     * @param projectId
     * @return
     */
    List<ProjectModuleQuestionnaireSelectedDTO> getProjectModuleQuestionnaireSelectedList(Integer projectId);

    /**
     * 根据code得到questionnaireId
     *
     * @return
     */
    Integer getQuestionnaireIdByCode(@Param("projectId") Integer projectId, @Param("code") String code);

    ProjectModuleDTO getProjectModuleInfo(@Param("projectId") Integer projectId);
}
