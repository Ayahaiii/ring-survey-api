package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleMoreTouchVO {

    private Integer id;

    private Integer projectId;

    private String sampleGuid;

    private String type;

    /**
     * 备注
     */
    private String description;

    /**
     * 接触状态
     * 0:失败，1:成功
     */
    private Integer status;

}
