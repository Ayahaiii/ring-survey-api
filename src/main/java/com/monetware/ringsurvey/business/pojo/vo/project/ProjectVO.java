package com.monetware.ringsurvey.business.pojo.vo.project;

import lombok.Data;

@Data
public class ProjectVO {

    private Integer id;

    private String type;

    private String name;

    private String description;

    private String labelText;

    private Integer qnaireType;

}
