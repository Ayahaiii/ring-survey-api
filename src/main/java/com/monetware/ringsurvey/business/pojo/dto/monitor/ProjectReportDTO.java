package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @ClassName ProjectMonitoringReport
 * @Author: zhangd
 * @Description: 获取项目监控报表数据
 * @Date: Created in 17:00 2020/2/24
 */
@Data
public class ProjectReportDTO {

    /**
     * 团队数量
     */
    private Integer numOfTeam;

    private Integer interviewerNum;

    /**
     * 样本数
     */
    private Integer numOfSample;

    private Integer finishNum;

    private Double finishRate;

    /**
     * 有效数量
     */
    private Integer numOfAnswer;

    /**
     * 答卷总数
     */
    private Integer responseNum;

    private Double successRate;

    /**
     * 总时长
     */
    private Long TimeLen;

    private String timeStr;

    private String avgTimeStr;

    /**
     * 信息总量
     */
    private Long fileSize;

    private String fileSizeStr;

    private String avgFileSizeStr;


}
