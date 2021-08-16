package com.monetware.ringsurvey.business.pojo.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectListDTO {

    private Integer id;

    private String name;// 项目名称

    private String type;// 类型

    private Integer status;// 状态

    private String labelText;// 标签

    private String role;

    private String roleName;

    private Integer quotaType;

    private Integer multipleQuestionnaire;// 项目问卷类型

    private Integer ifOpenSample;// 是否开启样本

    private String userName;

    private Integer projectRole;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    private String createTimeStr;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    private String updateTimeStr;

}
