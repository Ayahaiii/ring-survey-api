package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.sample.NumberRuleListDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseNumberRule;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface NumberRuleDao extends MyMapper<BaseNumberRule> {

    List<NumberRuleListDTO> getNumberRuleList(@Param("projectId") Integer projectId);

    Integer checkRuleExistByName(@Param("projectId") Integer projectId, @Param("name") String name, @Param("oldName") String oldName);

}
