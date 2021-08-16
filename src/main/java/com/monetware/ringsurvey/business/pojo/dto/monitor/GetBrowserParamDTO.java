package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/02 18:31
 * @Description: 获取浏览器参数传出dto
 **/
@Data
public class GetBrowserParamDTO {

    /**
     * 浏览器类型
     */
    private String explorerType;

    /**
     * 浏览器版本
     */
    private String explorerVersion;

    /**
     * count
     */
    private Integer count;
}
