package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.sample.InterviewerTravelDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseInterviewerTravel;
import com.monetware.ringsurvey.business.pojo.vo.sample.InterviewerTravelVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InterviewerTravelDao extends MyMapper<BaseInterviewerTravel> {

    List<InterviewerTravelDTO> getInterviewerTravelList(InterviewerTravelVO travelVO);

    List<InterviewerTravelDTO> getInterviewersLastedTravel(InterviewerTravelVO travelVO);

}
