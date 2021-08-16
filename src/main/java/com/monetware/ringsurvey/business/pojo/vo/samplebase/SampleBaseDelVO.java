package com.monetware.ringsurvey.business.pojo.vo.samplebase;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/4/3 17:01
 */
@Data
public class SampleBaseDelVO extends BaseVO {

    private List<Integer> sampleBaseIds;

}
