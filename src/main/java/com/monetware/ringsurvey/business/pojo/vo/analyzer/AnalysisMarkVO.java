package com.monetware.ringsurvey.business.pojo.vo.analyzer;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-30
 */
@Data
public class AnalysisMarkVO extends BaseVO {

    private Integer userId;

    private String type;

    private String variable;

    private Integer questionnaireId;

    private String answerCondition;

    private String sampleCondition;

    private String sort;

}
