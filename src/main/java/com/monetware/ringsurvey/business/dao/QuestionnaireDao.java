package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.qnaire.MyQnaireLabelListDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.MyQnaireListDTO;
import com.monetware.ringsurvey.business.pojo.dto.qnaire.QnaireSearchDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaire;
import com.monetware.ringsurvey.business.pojo.vo.questionnaire.MyQnaireListVO;
import com.monetware.ringsurvey.business.pojo.vo.qnaire.QnaireSearchVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface QuestionnaireDao extends MyMapper<BaseQuestionnaire> {

    List<QnaireSearchDTO> getProjectQnaireList(QnaireSearchVO searchVO);

    /**
     * 我的问卷列表
     *
     * @param myQnaireListVO
     * @return
     */
    List<MyQnaireListDTO> getMyQnaireList(MyQnaireListVO myQnaireListVO);

    List<MyQnaireListDTO> getMyQnaireListTest(@Param("userId") Integer userId);

    /**
     * 我的问卷标签列表
     *
     * @param userId
     * @return
     */
    List<MyQnaireLabelListDTO> getMyQnaireLabelList(@Param("userId") Integer userId);

}
