package com.monetware.ringsurvey.business.pojo.vo.questionnaire;

import lombok.Data;

@Data
public class MyQuestionnaireVO {

    private Integer id;

    private Integer projectId;

    private Integer moduleId;

    private String name;

    private String labelText;

    private String identity;

    private String xmlContent;

    private String logoUrl;

    private String bgUrl;

    private Integer questionNum;

    private Integer pageNum;

    private String welcomeText;

    private String endText;

    private Integer aloneWelcomeText;

    private Integer aloneEndText;

}
