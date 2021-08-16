package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleOutDataSaveVO extends BaseVO {

    private List<SampleOutDataVO> outDatas;
}
