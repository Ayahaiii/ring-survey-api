package com.monetware.ringsurvey.business.pojo.vo.redpacket;

import lombok.Data;

import java.util.List;

@Data
public class RedPacketRecordAuditVO {

    private List<Integer> ids;

    private Integer projectId;

    private Integer auditResult;

}
