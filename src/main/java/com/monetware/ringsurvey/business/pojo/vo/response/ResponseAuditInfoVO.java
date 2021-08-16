package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/27 11:19
 */
@Data
public class ResponseAuditInfoVO extends BaseVO {

    private Integer sampleId;

    private Integer questionnaireId;

    private Integer responseId;

    private String responseGuid;

    private Integer from;// 1-答卷列表 2-红包审核列表

    private Integer recordId;// 审核记录id

}
