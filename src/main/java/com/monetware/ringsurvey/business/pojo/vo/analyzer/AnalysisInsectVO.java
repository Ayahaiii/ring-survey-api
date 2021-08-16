package com.monetware.ringsurvey.business.pojo.vo.analyzer;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-30
 */
@Data
public class AnalysisInsectVO extends BaseVO {

    private Integer userId;

    private Integer questionnaireId;

    private String xType;

    private String xVariable;

    private String yType;

    private String yVariable;

    private String answerCondition;

    private String sampleCondition;

}
