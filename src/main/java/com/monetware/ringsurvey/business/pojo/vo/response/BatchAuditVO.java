package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/26 19:05
 */
@Data
public class BatchAuditVO extends BaseVO {

    private List<Integer> responseId;

    private Integer auditResult;

    private String auditComment;

    private String sampleGuid;

}
