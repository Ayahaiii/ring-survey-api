package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseAppVersion;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * @author Simo
 * @date 2019-02-27
 */
@Mapper
@Repository
public interface AppVersionDao extends MyMapper<BaseAppVersion> {


}