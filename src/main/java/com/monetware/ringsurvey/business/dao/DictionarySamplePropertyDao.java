package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseDictionarySampleProperty;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Linked
 * @date 2020/4/8 11:06
 */
@Mapper
@Repository
public interface DictionarySamplePropertyDao  extends MyMapper<BaseDictionarySampleProperty> {


}
