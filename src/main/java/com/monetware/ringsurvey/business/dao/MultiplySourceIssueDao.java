package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.issue.MultiplySourceIssueDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseMultiplySourceIssue;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MultiplySourceIssueDao extends MyMapper<BaseMultiplySourceIssue> {

    List<MultiplySourceIssueDTO> getMultiplySourceIssues(@Param("projectId") Integer projectId);

}
