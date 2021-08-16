package com.monetware.ringsurvey.business.pojo.dto.issue;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 发布日志DTO
 */
@Data
public class PublishLogDTO {

    private Integer id;

    private Integer type;// 类型 1-短信 2-邮件

    private Integer status;// 状态

    private String publishTo;// 样本手机或者邮箱

    private String sampleName;// 样本姓名

    private String subject;// 主题

    private String content;// 正文

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String createTimeStr;// 发送时间

}
