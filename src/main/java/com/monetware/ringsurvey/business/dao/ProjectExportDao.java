package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.history.ExportHistoryDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectExportHistory;
import com.monetware.ringsurvey.business.pojo.vo.history.HistoryListVO;
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
public interface ProjectExportDao extends MyMapper<BaseProjectExportHistory> {

    List<ExportHistoryDTO> getExportHistory(HistoryListVO listVO);

}
