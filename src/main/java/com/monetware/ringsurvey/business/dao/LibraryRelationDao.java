package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseLibraryRelation;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * @author Linked
 * @date 2020/6/1 11:40
 */
@Mapper
@Repository
public interface LibraryRelationDao extends MyMapper<BaseLibraryRelation> {


}
