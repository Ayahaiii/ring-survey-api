package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.monitor.IndexDynamicDTO;
import com.monetware.ringsurvey.business.pojo.dto.monitor.SampleCompleteDTO;
import com.monetware.ringsurvey.business.pojo.dto.monitor.SampleStatusUseDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleStatusRecord;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Linked
 * @date 2020/2/17 18:28
 */
@Mapper
@Repository
public interface ProjectSampleStatusRecordDao extends MyMapper<BaseProjectSampleStatusRecord> {

    List<SampleStatusUseDTO> getSampleStatusUseList(@Param("projectId") Integer projectId, @Param("type") Integer type,
                                                    @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    //============================================ lu Begin =======================================

    /**
    * @Author: lu
    * @Date: 2020/4/1  上午11:14
    * @Description:获取样本绩效动态
    **/
    List<SampleCompleteDTO> getSampleCompleteList(@Param("projectId") Integer projectId, @Param("type") Integer type,
                                                  @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                  @Param("interviewerIdList") List<Integer> interviewerIdList);
    
    /**
    * @Author: lu
    * @Date: 2020/4/2  下午3:45
    * @Description:获取指标动态
    **/
    List<IndexDynamicDTO> getIndexDynamic(@Param("projectId") Integer projectId, @Param("type") Integer type,
                                          @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                          @Param("totalCount") Integer totalCount);
    //============================================ lu End =========================================

}
