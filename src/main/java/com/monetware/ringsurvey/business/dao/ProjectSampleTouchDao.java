package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleTouch;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Simo
 * @date 2019-06-17
 */
@Mapper
@Repository
public interface ProjectSampleTouchDao extends MyMapper<BaseProjectSampleTouch> {


}
