package com.monetware.ringsurvey.business.pojo.vo.issue;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

@Data
public class MultiplySourceIssueVO extends BaseVO {

    private Integer id;

    private String name;

    private String code;

    private Integer teamUserId;

}
