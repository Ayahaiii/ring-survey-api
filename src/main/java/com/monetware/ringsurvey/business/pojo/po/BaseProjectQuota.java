package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 问卷配额
 */
@Data
@Table(name = "rs_project_quota")
public class BaseProjectQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer projectId;
    private String name;

    private Integer questionnaireId;
    /**
     * 1:问卷配额 2:样本配额 3:问卷配额+样本配额
     */
    private Integer type;
    private String questionnaireQuota;// 问卷配额
    private String questionnaireQuotaSurvml;// 问卷配额的xml形式
    private String ruleSurvmlId;// 问卷配额形成规则的id
    private String sampleQuota;// 样本配额
    private Integer upperLimit;// 上限
    private Integer lowerLimit;// 下限
    private Integer currentQuantity;// 当前满足配额的答卷数量
    private Integer status;// 配额状态 0-禁用 1-启用
    private Integer createUser;
    private Date createTime;
    private Integer lastModifyUser;
    private Date lastModifyTime;
}
