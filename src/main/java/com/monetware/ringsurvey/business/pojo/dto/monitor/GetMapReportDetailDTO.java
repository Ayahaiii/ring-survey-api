package com.monetware.ringsurvey.business.pojo.dto.monitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: lu
 * @Date: 2020/04/03 15:33
 * @Description: 获取数据地图详细信息
 **/
@Data
public class GetMapReportDetailDTO {

    /**
     * 答卷guid
     */
    private String responseGuid;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 地址
     */
    private String address;

    /**
     * 定位时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date posTime;

    /**
     * 负责人姓名
     */
    private String  leaderName;

    /**
     * 模块名
     */
    private String moduleName;
}
