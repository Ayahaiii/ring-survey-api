package com.monetware.ringsurvey.business.pojo.vo.analyzer;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-30
 */
@Data
public class AnalysisInsectSearchVO extends AnalysisInsectVO {

    private String qVariable;

    private String sVariable;

    private String findFields;

}
