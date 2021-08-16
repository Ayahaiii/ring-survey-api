package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseBaseSample;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Linked
 * @date 2020/4/8 17:55
 */
@Mapper
@Repository
public interface BaseSampleDao extends MyMapper<BaseBaseSample> {

}
