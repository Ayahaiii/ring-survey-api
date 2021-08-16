package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.compiler.SurvmlModel;
import com.monetware.ringsurvey.survml.questions.Question;
import lombok.Data;

import java.util.List;

/**
 * 问卷解析对象
 */
@Data
public class QuestionnaireParseDTO {

    private SurvmlModel survmlModel;

    private SurvMLDocument survMLDocument;

    private List<SurvmlModel.DVariable> dVariableList;

    private List<SurvmlModel.DQuestion> dQuestionList;

    private List<Question> questions;

}
