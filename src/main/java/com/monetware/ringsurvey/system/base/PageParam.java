package com.monetware.ringsurvey.system.base;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Cookie
 * @Date: 2019/1/15 10:37
 * @Description: 分页参数实体类 用于VO继承
 */
@Data
public class PageParam extends BaseVO implements Serializable {


    @ApiModelProperty(value = "当前页")
    private int pageNum = 1;

    @ApiModelProperty(value = "查询的记录数")
    private int pageSize = 10;

}
