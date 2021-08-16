package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-27
 */
@Data
public class SampleMoreContactListVO extends BaseVO {

    private String sampleGuid;

    private List<SampleMoreContactVO> contactList;

}
