package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * 答卷审核编辑VO
 */
@Data
public class ResponseAuditEditVO extends BaseVO {

    private Integer responseId;

    private String responseGuid;

}
