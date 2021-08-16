package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;

/**
 * 问卷模块DTO(用于多问卷、单问卷选择下拉框中)
 */
@Data
public class ProjectModuleQuestionnaireSelectedDTO {

    private Integer projectId;

    private Integer moduleId;

    private String name;

    private String moduleName;

    private Integer questionnaireId;

    private Integer status;

    private Integer version;

}
