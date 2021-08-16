package com.monetware.ringsurvey.business.pojo.vo.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/03 15:37
 * @Description: 获得地图详细信息传入vo
 **/
@Data
public class GetMapReportDetailVO {

    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 答卷guid
     */
    private String responseGuid;
}
