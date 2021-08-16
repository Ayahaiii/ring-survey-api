package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.issue.PublishLogDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectPublishLog;
import com.monetware.ringsurvey.business.pojo.vo.issue.PublishSearchVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/25 19:16
 */
@Mapper
@Repository
public interface ProjectPublishLogDao extends MyMapper<BaseProjectPublishLog> {

    List<PublishLogDTO> getPublishLogs(PublishSearchVO searchVO);

}
