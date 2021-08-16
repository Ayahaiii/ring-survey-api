package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;

@Data
public class QuestionnaireInitDTO {

    private Integer id;

    private String name;

    private String labelText;// 问卷标签

    private String identity;// 问卷标识

    private Integer isLibrary;// 是否问卷库

    private String logoUrl;

    private String bgUrl;

    private Integer questionNum;// 问题数量

    private Integer pageNum;// 分页数量

    private String welcomeText;

    private String endText;

}
