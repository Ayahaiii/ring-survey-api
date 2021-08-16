package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectPublishTask;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Linked
 * @date 2020/3/25 19:16
 */
@Mapper
@Repository
public interface ProjectPublishTaskDao extends MyMapper<BaseProjectPublishTask> {

}
