package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.monitor.ProjectReportDTO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectFinishDTO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectHeadDTO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectInfoDTO;
import com.monetware.ringsurvey.business.pojo.dto.project.ProjectListDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProject;
import com.monetware.ringsurvey.business.pojo.vo.project.ProjectListVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2019-06-17
 */
@Mapper
@Repository
public interface ProjectDao extends MyMapper<BaseProject> {

    ProjectHeadDTO getProjectNameAndRole(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    ProjectInfoDTO getProjectInfo(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    List<ProjectListDTO> getProjectList(ProjectListVO projectListVO);

    List<ProjectListDTO> getProjectListTest(@Param("userId") Integer userId);

    ProjectReportDTO getProjectReport(@Param("projectId") Integer projectId);

    ProjectReportDTO getProjectReportByUserId(@Param("projectId") Integer projectId, @Param("userId") Integer userId);

    Integer updateProjectAdd(BaseProject project);

    Integer updateProjectDel(BaseProject project);

    List<ProjectFinishDTO> getProjectByFinish();

    List<Map<String, Object>> getLast7DayCount(@Param("dateStr") Date dateStr);

    List<Map<String, Object>> getProjectTotalResult(@Param("startDate") String startDate);

    List<Map<String, Object>> getProjectNewResult(@Param("startDate") String startDate);

    Map<String, Integer> getProjectCount();

    List<Map<String, Integer>> getProjectCountByUserId(@Param("userId") Integer userId);

}
