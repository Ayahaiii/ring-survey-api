package com.monetware.ringsurvey.business.pojo.dto.issue;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class SmsEmailViewDTO {

    private BigDecimal balance;

    private Integer num;

    private Integer status;

    private Integer type;

}
