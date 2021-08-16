package com.monetware.ringsurvey.business.pojo.vo.issue;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

import java.util.Date;

/**
 * 发布日志检索VO
 */
@Data
public class PublishSearchVO extends PageParam {

    private Integer status;

    private Integer type;

    private String keyWord;

    private Date startCreateTime;

    private Date endCreateTime;

}
