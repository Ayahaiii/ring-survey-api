package com.monetware.ringsurvey.business.pojo.vo.qnaire;

import lombok.Data;

/**
 * 项目问卷模块VO
 */
@Data
public class ProjectModuleVO {

    private Integer id;

    private Integer projectId;

    private String code;

    private String name;

    private Integer type;// 模块类型

    private Integer maxVersion;

    private Integer moduleDependency;// 依赖关系(依赖的模块Id)

    private String sampleDependency;// 样本某一属性 逻辑运算符 值(例：name = "张三")

    private Integer quotaMin;// 允许单个样本提交的最小答卷数(主模块只能1)

    private Integer quotaMax;// 允许单个样本提交的最大答卷数(主模块只能1)

    private Integer isAllowedManualAdd;// 子问卷是否允许手动新增答卷

    private Integer questionnaireId;// 引用导入的问卷Id

    private Integer groupId;// 分组Id

}
