package com.monetware.ringsurvey.business.pojo.dto.sample;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleTouch;
import lombok.Data;

@Data
public class SampleTouchDTO extends BaseProjectSampleTouch {

    private String createUserStr;

    private String updateUserStr;

    private String deleteUserStr;

}
