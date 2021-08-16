package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/24 13:23
 */
@Data
public class ResponseListVO extends PageParam {

    /***
     * 答卷状态
     */
    private Integer responseStatus;

    private Integer userId;


    /**
     * 是否抽审 0:否 1:是
     */
    private Integer samplingStatus;

    /**
     * 修改开始时间
     */
    private String modifyStartTime;

    /**
     * 修改结束时间
     */
    private String modifyEndTime;

    /**
     * 审核开始时间
     */
    private String auditStartTime;

    /**
     * 审核结束时间
     */
    private String auditEndTime;

    /**
     * 样本标识
     */
    private String sampleMark;

    /**
     *
     * 答卷标识
     */
    private String responseMark;

    /**
     * 问卷id
     */
    private Integer questionnaireId;

    private String answerCondition;

    private String sampleCondition;

    private Integer interviewerId;

    private String auditor;

    private String keyword;

    private String endBeginTime;

    private String endEndTime;

    private String beginModifyTime;

    private String endModifyTime;

}
