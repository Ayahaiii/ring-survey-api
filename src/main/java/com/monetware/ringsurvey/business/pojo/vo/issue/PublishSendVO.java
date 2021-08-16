package com.monetware.ringsurvey.business.pojo.vo.issue;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class PublishSendVO {

    private Integer projectId;

    private Integer type;// 1-短信 2-邮件

    private Integer method;// 1-全部样本 2-所选样本

    private List<Integer> sampleIds;

    private String answerUrl;

    private Integer isTask;// 0-立即发送 1-定时任务

    private String sendTime;

    private Integer sendCountPer;// 发送数量

    private Integer sendInterval;// 发送间隔

}
