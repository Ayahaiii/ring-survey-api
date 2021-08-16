package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/20 20:31
 */
@Data
public class PropertyTemplateVO extends BaseVO {

    private String name;

    /**
     * 项目使用属性
     */
    private String useProperty;

    /**
     * 列表展示属性
     */
    private String listProperty;

    /**
     * 列表展示属性
     */
    private String markProperty;

}
