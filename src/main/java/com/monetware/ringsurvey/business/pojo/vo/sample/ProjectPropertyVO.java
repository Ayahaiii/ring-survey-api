package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/20 18:06
 */
@Data
public class ProjectPropertyVO {

    private Integer id;

    private Integer projectId;

    /**
     * 项目使用属性
     */
    private String useProperty;

    /**
     * 列表展示属性
     */
    private String listProperty;

    /**
     * 样本标示属性
     */
    private String markProperty;

}
