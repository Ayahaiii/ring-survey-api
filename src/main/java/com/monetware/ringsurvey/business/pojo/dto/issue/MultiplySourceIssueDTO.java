package com.monetware.ringsurvey.business.pojo.dto.issue;

import lombok.Data;

@Data
public class MultiplySourceIssueDTO {

    private Integer id;

    private Integer projectId;

    private String name;

    private String code;

    private Integer teamUserId;

    private String teamUserName;

    private Integer isDelete;

}
