package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 问卷资源Base
 */
@Data
@Table(name = "rs_questionnaire_resource")
public class BaseQnaireResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer type;

    private String name;

    private String url;

    private String tagId;// [1,2,3,...]

    private Integer userId;

}
