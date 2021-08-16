package com.monetware.ringsurvey.system.util.survml;

import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionnaireParseDTO;
import com.monetware.ringsurvey.survml.SurvMLDocument;
import com.monetware.ringsurvey.survml.SurvMLDocumentParser;
import com.monetware.ringsurvey.survml.compiler.SurvmlModel;
import com.monetware.ringsurvey.survml.compiler.SurvmlParse;
import com.monetware.ringsurvey.survml.questions.Question;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 问卷引擎工具
 */
public class SurvmlUtil {

    /**
     * 问卷解析
     *
     * @param xml
     * @return
     */
    public static QuestionnaireParseDTO getQuestionsInfoByXml(String xml) {
        QuestionnaireParseDTO parseDTO = new QuestionnaireParseDTO();

        SurvmlParse survmlParse = new SurvmlParse();
        SurvmlModel survmlModel = new SurvmlModel();
        SurvMLDocument survMLDocument = null;
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xml.getBytes("utf-8"));
            survmlModel = survmlParse.parseWithInputStream(inputStream);
            survMLDocument = new SurvMLDocumentParser(xml).parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<SurvmlModel.DVariable> variableList = survmlModel.variables;
        List<SurvmlModel.DQuestion> questionList = survmlModel.questions;
        List<Question> questions = survMLDocument.getQuestions();

        parseDTO.setSurvmlModel(survmlModel);
        parseDTO.setSurvMLDocument(survMLDocument);
        parseDTO.setDVariableList(variableList);
        parseDTO.setDQuestionList(questionList);
        parseDTO.setQuestions(questions);

        return parseDTO;
    }

}
