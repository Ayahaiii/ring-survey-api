package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class BaseProjectSampleOutData {

    private List<String> variables;

    private Map<String, Map<String, String>> datas;

}
