package com.monetware.ringsurvey.business.pojo.vo.user;

import lombok.Data;

/**
 * 退款VO
 */
@Data
public class UserReFundVO {

    private Integer projectId;

    private Integer refundType;// 1-红包 2-短信 3-邮件

}
