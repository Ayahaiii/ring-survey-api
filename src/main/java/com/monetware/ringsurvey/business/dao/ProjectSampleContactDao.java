package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleContact;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Simo
 * @date 2019-06-17
 */
@Mapper
@Repository
public interface ProjectSampleContactDao extends MyMapper<BaseProjectSampleContact> {


}
