package com.monetware.ringsurvey.business.pojo.dto.sample;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleAddress;
import lombok.Data;

@Data
public class SampleAddressDTO extends BaseProjectSampleAddress {

    private String createUserStr;

    private String updateUserStr;

    private String deleteUserStr;

}
