package com.monetware.ringsurvey.business.pojo.dto.monitor;


import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/01 11:16
 * @Description: 查询样本完成情况传出dto
 **/
@Data
public class SampleCompleteDTO {

    private String timeStr;

    private Integer totalCount;

    private Integer interviewerId;

    private String interviewerName;
}
