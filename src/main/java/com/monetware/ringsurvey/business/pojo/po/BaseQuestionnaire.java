package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rs_questionnaire")
public class BaseQuestionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String labelText;// 问卷标签

    private String identity;// 问卷标识

    private String xmlContent;

    private Integer isLibrary;// 是否问卷库

    private String logoUrl;

    private String bgUrl;

    private Integer questionNum;// 问题数量

    private Integer pageNum;// 分页数量

    private String welcomeText;

    private Integer aloneWelcomeText;// 欢迎语是否独立

    private String endText;

    private Integer aloneEndText;// 结束语是否独立

    private Integer createUser;

    private Date createTime;

    private Integer lastModifyUser;

    private Date lastModifyTime;

    private Integer isDelete;

    private Integer deleteUser;

    private Date deleteTime;

}
