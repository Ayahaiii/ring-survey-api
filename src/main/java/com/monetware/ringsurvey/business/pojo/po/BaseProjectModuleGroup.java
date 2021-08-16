package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 项目问卷分组
 */
@Data
@Table(name = "rs_project_module_group")
public class BaseProjectModuleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private String name;

    private String description;

    private Integer createUser;

    private Date createTime;

    private Integer lastModifyUser;

    private Date lastModifyTime;

}
