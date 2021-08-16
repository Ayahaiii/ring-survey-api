package com.monetware.ringsurvey.business.pojo.vo.qnaire;

import lombok.Data;

/**
 * 问卷模块分组VO
 */
@Data
public class ProjectModuleGroupVO {

    private Integer id;

    private Integer projectId;

    private String name;

    private String description;

}
