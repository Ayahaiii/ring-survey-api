package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 项目问卷模块
 */
@Data
@Table(name = "rs_project_module")
public class BaseProjectModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private String code;// 问卷模块编号(A-Z)

    private String name;

    private Integer type;// 模块类型(主、子)

    private Integer maxVersion;

    private Integer moduleDependency;// 依赖关系(依赖的模块Id)

    private String sampleDependency;// 样本某一属性 逻辑运算符 值(例：name = "张三")

    private Integer quotaMin;// 允许单个样本提交的最小答卷数

    private Integer quotaMax;// 允许单个样本提交的最大答卷数

    private Integer isAllowedManualAdd;// 子问卷是否允许手动新增答卷

    private Integer groupId;

    private Integer status;

    private String editContent;

    private Integer editFlag;

    private Integer createUser;

    private Date createTime;

    private Integer lastModifyUser;

    private Date lastModifyTime;

    private Integer isDelete;

    private Integer deleteUser;

    private Date deleteTime;

}
