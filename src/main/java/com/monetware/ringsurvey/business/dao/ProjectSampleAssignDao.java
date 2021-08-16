package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.response.InterviewerDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseSampleAssignment;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Simo
 * @date 2019-06-17
 */
@Mapper
@Repository
public interface ProjectSampleAssignDao extends MyMapper<BaseSampleAssignment> {
    /**
     * 访问员列表
     *
     * @param projectId
     * @return
     */
    List<InterviewerDTO> getInterviewList(@Param("projectId") Integer projectId);

    /**
     * 批量删除分派
     *
     * @param projectId
     * @param list
     * @param userId
     * @return
     */
    int deleteBySampleGuids(@Param("projectId") Integer projectId, @Param("list") List<String> list, @Param("userId") Integer userId);

}
