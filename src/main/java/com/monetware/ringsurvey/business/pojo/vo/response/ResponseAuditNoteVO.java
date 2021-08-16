package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/4/14 17:10
 */
@Data
public class ResponseAuditNoteVO extends BaseVO {

    private Integer responseId;

    private String qId;

    private String auditNotes;


}
