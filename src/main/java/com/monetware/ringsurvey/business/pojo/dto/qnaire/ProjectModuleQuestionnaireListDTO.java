package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 多问卷模块列表DTO
 */
@Data
public class ProjectModuleQuestionnaireListDTO {

    private Integer id;

    private Integer qnaireId;// 源问卷Id

    private String groupName;

    private String code;

    private String name;

    private Integer maxVersion;

    private Integer type;

    private Integer questionNum;

    private Integer pageNum;

    private Integer userId;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer answerNum;// 答卷数量

    private Integer status;// 状态 0-草稿 1-发布

    private Integer editFlag;

    private String editContent;

    private String moduleDependency;

    private String sampleDependency;

    private Integer quotaMax;

    private Integer quotaMin;

    private Integer manualAdd;

    private boolean hasQuestions;

}
