package com.monetware.ringsurvey.business.pojo.vo.project;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectListVO extends PageParam {

    private String searchType;

    private Integer userType;

    private Integer userId;

    private String keyword;

    private String type;

    private String labelText;

    private Integer status;

    private String name;

    private String userName;

    private Date startRunTime;

    private Date endRunTime;

    private Date startCreateTime;

    private Date endCreateTime;

    private Date startStopTime;

    private Date endStopTime;

    private Date startUpdateTime;

    private Date endUpdateTime;

    private Integer multipleQuestionnaire;// 单、多问卷

}
