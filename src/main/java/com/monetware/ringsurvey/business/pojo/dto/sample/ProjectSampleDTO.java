package com.monetware.ringsurvey.business.pojo.dto.sample;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleAddress;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleContact;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSampleTouch;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/18 21:45
 */
@Data
public class ProjectSampleDTO extends BaseProjectSample {

    private List<BaseProjectSampleAddress> addressList;

    private List<BaseProjectSampleContact> contactList;

    private List<BaseProjectSampleTouch> touchList;

    private String managerName;

    private List<String> assNames;

}
