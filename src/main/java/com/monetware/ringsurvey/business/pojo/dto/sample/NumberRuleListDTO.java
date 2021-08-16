package com.monetware.ringsurvey.business.pojo.dto.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class NumberRuleListDTO {

    private Integer id;

    private Integer projectId;

    private String name;

    private String areaCode;

    private String phoneNumber;

    private String extNum;

    private Integer serialNo;

    private Integer createUser;

    private String createUserStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String createTimeStr;

    private String updateUser;

    private String updateUserStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String updateTimeStr;

    private Integer isDelete;

}
