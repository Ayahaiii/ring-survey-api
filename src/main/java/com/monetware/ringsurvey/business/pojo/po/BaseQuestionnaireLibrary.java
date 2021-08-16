package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "rs_questionnaire_library")
public class BaseQuestionnaireLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer questionnaireId;

    private String name;

    private String description;

    private Integer status;// 状态

    private String xmlContent;

    private Double rate;

    private BigDecimal price;

    private String imageUrl;

    private Integer ifFree;

    private Integer sales;

    private Integer starCount;

    private Integer viewCount;

    private Integer commentCount;

    private Integer createUser;

    private Date createTime;

    private String logoUrl;

    private String bgUrl;

    private Integer questionNum;// 问题数量

    private Integer pageNum;// 分页数量

    private String welcomeText;

    private String endText;

    private String labelText;// 问卷标签

}
