package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.response.InterviewerDTO;
import com.monetware.ringsurvey.business.pojo.dto.response.ResponseAudioListDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseResponseAudio;
import com.monetware.ringsurvey.business.pojo.vo.response.ResponseFileListVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/25 11:25
 */
@Mapper
@Repository
public interface ProjectResponseAudioDao extends MyMapper<BaseResponseAudio> {


    List<ResponseAudioListDTO> getResponseAudioList(ResponseFileListVO listVO);


    List<InterviewerDTO> getInterviews(@Param("projectId") Integer projectId);
}
