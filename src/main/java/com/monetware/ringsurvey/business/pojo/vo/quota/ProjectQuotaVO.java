package com.monetware.ringsurvey.business.pojo.vo.quota;

import lombok.Data;

/**
 * @author Linked
 */
@Data
public class ProjectQuotaVO {


    private Integer id;

    private Integer projectId;

    private Integer type;

    private String name;
    /**
     * 问卷配额
     */
    private String questionnaireQuota;

    /**
     * 添加问卷的配额id
     */
    private Integer questionnaireId;

    /**
     *  样本配额
     */
    private String sampleQuota;

    /**
     *  上限
     */
    private Integer upperLimit;

    /**
     * 下限
     */
    private Integer lowerLimit;

    /**
     * 问卷配额的xml形式
     */
    private String questionnaireQuotaSurvml;

    /**
     *  问卷配额形成规则的id
     */
    private String ruleSurvmlId;

}
