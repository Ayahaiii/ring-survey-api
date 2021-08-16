package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectQuestionnaire;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProjectQuestionnaireDao extends MyMapper<BaseProjectQuestionnaire> {
}