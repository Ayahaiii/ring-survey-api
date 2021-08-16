package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class ProjectModuleHistoryDTO {

    private Integer moduleId;

    private String code;

    private String name;

    private Integer questionnaireId;

    private Integer questionNum;

    private Integer pageNum;

    private Integer version;

    private Integer createUser;

    private String createUserStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String updateUser;

    private String updateUserStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer responseCount;

    private String remark;

}
