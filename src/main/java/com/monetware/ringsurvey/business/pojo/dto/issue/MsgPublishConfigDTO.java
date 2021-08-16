package com.monetware.ringsurvey.business.pojo.dto.issue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetware.ringsurvey.business.pojo.po.publishConfig.ZtMessageConfig;
import lombok.Data;

import java.util.Date;

/**
 * 短信发布配置DTO
 */
@Data
public class MsgPublishConfigDTO {

    private Integer id;

    private Integer status;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifyTime;

    private Integer testSuccess;

    private ZtMessageConfig config;

}
