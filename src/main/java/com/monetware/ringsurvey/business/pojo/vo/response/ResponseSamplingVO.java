package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/24 16:40
 */
@Data
public class ResponseSamplingVO extends BaseVO {

    private List<Integer> responseIds;

    private Integer samplingCount;

    private Integer samplingPercent;

}
