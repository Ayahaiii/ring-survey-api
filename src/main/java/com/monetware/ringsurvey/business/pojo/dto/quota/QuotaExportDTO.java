package com.monetware.ringsurvey.business.pojo.dto.quota;

import lombok.Data;

/**
 * @author Linked
 * @date 2020/4/13 16:46
 */
@Data
public class QuotaExportDTO {

    private String name;

    private Integer lowerLimit;

    private Integer upperLimit;

    private String sampleQuota;

    private String questionnaireQuota;

    private String code;


}
