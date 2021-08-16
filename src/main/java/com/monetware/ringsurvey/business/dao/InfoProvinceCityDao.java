package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseInfoProvinceCity;
import com.monetware.ringsurvey.business.pojo.vo.sample.TownVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:28
 */
@Mapper
@Repository
public interface InfoProvinceCityDao extends MyMapper<BaseInfoProvinceCity> {

    List<String> getTownNameList(TownVO townVO);

}
