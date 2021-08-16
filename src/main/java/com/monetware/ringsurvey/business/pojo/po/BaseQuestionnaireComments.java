package com.monetware.ringsurvey.business.pojo.po;


import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rs_questionnaire_comments")
public class BaseQuestionnaireComments {

    private Integer id;

    private Integer replyId;

    private Integer libraryId;

    private Integer userId;

    private Integer commentedId;

    private String content;

    private Double score;

    private Date createTime;


}
