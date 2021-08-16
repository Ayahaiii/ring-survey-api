package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/4/10 17:57
 */
@Data
public class ResponseAuditVO extends BaseVO {

    private Integer responseId;

    /**
     * 1:有效 2:无效 3:打回
     */
    private Integer auditResult;

    /**
     * 审核评分
     * A:优秀 B:良好 C:中等 D:较差
     */
    private String auditScore;

    private Integer sampleId;

    private Integer auditMsg;

    private String auditQuestions;

    private String auditComments;

    //private String auditReturnReason;

    //private String responseJsonData;

    // private Integer questionnaireId;

    //private String guid;

    private String sampleGuid;

}
