package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 我的问卷列表DTO
 */
@Data
public class MyQnaireListDTO {

    private Integer id;

    private String name;

    private Integer isLibrary;

    private String labelText;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String createTimeStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifyTime;

    private String lastModifyTimeStr;

}
