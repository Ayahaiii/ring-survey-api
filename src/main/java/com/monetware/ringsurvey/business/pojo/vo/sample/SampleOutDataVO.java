package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleOutDataVO {

    private String code;

    private String sampleGuid;

    private List<String> variables;

    private Map<String, Map<String, String>> datas;

}
