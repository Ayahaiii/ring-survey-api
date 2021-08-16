package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/03 13:26
 * @Description: 获取地图报告传出dto
 **/
@Data
public class GetMapReportDTO {

    /**
     * 答卷guid
     */
    private String responseGuid;

    /**
     * 负责人姓名
     */
    private String  leaderName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 数量总数
     */
    private Integer posCount;
}
