package com.monetware.ringsurvey.business.pojo.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-24
 */
@Data
public class ProjectHeadDTO {

    private String name;

    private String roleId;

    private Integer projectRole;

    private String type;

    private String inviteCode;

    private Integer autoAudit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    private Integer createUser;

    private String allProperty;

    private String useProperty;// 项目使用属性

    private String markProperty;

    private ProjectConfigDTO config;

}
