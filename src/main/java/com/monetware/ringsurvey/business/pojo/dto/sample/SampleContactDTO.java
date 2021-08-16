package com.monetware.ringsurvey.business.pojo.dto.sample;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleContact;
import lombok.Data;

@Data
public class SampleContactDTO extends BaseProjectSampleContact {

    private String createUserStr;

    private String updateUserStr;

    private String deleteUserStr;

}
