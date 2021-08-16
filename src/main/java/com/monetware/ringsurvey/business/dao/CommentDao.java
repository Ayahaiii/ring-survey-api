package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.library.QnaireCommentsDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaireComments;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/6/1 11:40
 */
@Mapper
@Repository
public interface CommentDao extends MyMapper<BaseQuestionnaireComments> {

    List<QnaireCommentsDTO> getCommentsInfo(@Param("libraryId") Integer libraryId);

}
