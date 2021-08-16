package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 项目问卷
 */
@Data
@Table(name = "rs_project_questionnaire")
public class BaseProjectQuestionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer projectId;

    private Integer moduleId;// 项目问卷模块Id

    private Integer questionnaireId;// 源问卷Id

    private Integer version;// 问卷版本

    private String code;// 对应的发布code

    private String xmlContent;

    private Integer questionNum;// 问题数量

    private Integer pageNum;// 分页数量

    private String welcomeText;

    private Integer aloneWelcomeText;

    private String endText;

    private Integer aloneEndText;

    private String logoUrl;

    private String bgUrl;

    private Integer createUser;

    private Date createTime;

    private Integer lastModifyUser;

    private Date lastModifyTime;

    private Integer isDelete;

    private Integer deleteUser;

    private Date deleteTime;

    private String remark;// 版本升级备注

}
