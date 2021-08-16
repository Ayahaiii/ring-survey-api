package com.monetware.ringsurvey.business.pojo.vo.quota;

import lombok.Data;
import org.dom4j.rule.Rule;


/**
 * @author Linked
 * @date 2020/3/23 14:18
 */
@Data
public class QuotaImportVO {



    private String name;

    /**
     * 问卷编号
     */
    private String code;

    /**
     * 问卷配额
     */
    private String questionnaireQuota;

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

    private String questionnaireQuotaSurvml;

    private String ruleSurvmlId;


}
