package com.monetware.ringsurvey.business.pojo.dto.quota;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectQuotaDTO {
    private Integer id;
    private String name;
    private Integer type;

    private Integer questionnaireId;

    private String questionnaireQuota;// 问卷条件
    private String sampleQuota;// 样本条件
    private Integer upperLimit;// 上限
    private Integer lowerLimit;// 下限
    private Integer currentQuantity;// 当前满足配额的答卷数量
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
