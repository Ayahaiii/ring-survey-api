package com.monetware.ringsurvey.business.pojo.vo.project;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectConfig;
import lombok.Data;

@Data
public class ProjectConfigVO extends BaseProjectConfig {

    private Integer projectId;

    private String type;

    private Integer role;

}
