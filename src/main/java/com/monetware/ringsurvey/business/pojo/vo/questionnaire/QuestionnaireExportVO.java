package com.monetware.ringsurvey.business.pojo.vo.questionnaire;

import lombok.Data;

/**
 * 问卷导出VO
 */
@Data
public class QuestionnaireExportVO {

    private Integer projectId;

    private Integer questionnaireId;

    private String fileType;

    private Integer moduleId;
    
}
