package com.monetware.ringsurvey.business.pojo.dto.quota;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/26 10:28
 */
@Data
public class ModuleSampleDTO {


    private List<QuotaModuleListDTO> quotaModuleList;

    private String sampleProperty;


}
