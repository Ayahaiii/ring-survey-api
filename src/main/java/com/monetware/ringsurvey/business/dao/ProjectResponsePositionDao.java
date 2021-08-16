package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.monitor.GetMapReportDTO;
import com.monetware.ringsurvey.business.pojo.dto.monitor.GetMapReportDetailDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseResponsePosition;
import com.monetware.ringsurvey.business.pojo.vo.monitor.GetMapReportDetailVO;
import com.monetware.ringsurvey.business.pojo.vo.monitor.GetMapReportVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lujj on 2020/4/3.
 */
@Mapper
@Repository
public interface ProjectResponsePositionDao extends MyMapper<BaseResponsePosition> {

    //============================================ lu Begin =======================================

    /**
    * @Author: lu
    * @Date: 2020/4/3  下午1:21
    * @Description:获取地图报告
    **/
    List<GetMapReportDTO> getMapReport (GetMapReportVO param);

    /**
    * @Author: lu
    * @Date: 2020/4/3  下午3:40
    * @Description:获取地图报告详细信息
    **/
    List<GetMapReportDetailDTO> getMapReportDetail(GetMapReportDetailVO param);
    //============================================ lu End =========================================
}
