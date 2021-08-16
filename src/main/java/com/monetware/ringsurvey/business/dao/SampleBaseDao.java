package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.samplebase.SampleBaseListDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseSampleBase;
import com.monetware.ringsurvey.business.pojo.vo.samplebase.SampleBaseListVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/4/1 16:32
 */
@Mapper
@Repository
public interface SampleBaseDao extends MyMapper<BaseSampleBase> {

    /**
     * 样本库列表
     * @param listVO
     * @return
     */
    List<SampleBaseListDTO> getSampleBaseList(SampleBaseListVO listVO);

}
