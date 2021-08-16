package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-24
 */
@Data
public class AnswerLonALatDTO {

    /**
     * 样本id
     */
    private Integer id;

    private String sampleCode;

    private String sampleName;

    private String lon;

    private String lat;

}
