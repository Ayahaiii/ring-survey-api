package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;


/**
 * 项目问卷模块、问卷DTO(多问卷、单问卷通用)
 */
@Data
public class ProjectModuleQuestionListDTO {

    private Integer id;

    private String name;

    private Integer questionnaireId;

    private String xmlContent;

    private Integer version;

}
