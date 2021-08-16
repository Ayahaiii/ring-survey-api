package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.response.ResponseHistoryListDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseResponseHistory;
import com.monetware.ringsurvey.business.pojo.vo.response.ResponseHistoryListVO;
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
public interface ProjectResponseHistoryDao extends MyMapper<BaseResponseHistory> {


    List<ResponseHistoryListDTO> getResponseHistoryList(ResponseHistoryListVO listVO);

}
