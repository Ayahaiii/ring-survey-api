package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rs_project_publish_task")
public class BaseProjectPublishTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;
    
    /**
     * 任务名
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroupName;

    /**
     * 触发名
     */
    private String triggerName;

    /**
     * 触发组名
     */
    private String triggerGroupName;

    /**
     * 定时时间表达式
     */
    private String cron;

    /**
     * 发送数量
     */
    private Integer sendCountPer;

    private Integer createUser;

    private Date createTime;

}
