package com.monetware.ringsurvey.business.pojo.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Simo
 * @date 2019-09-20
 */
@Data
public class UserOrderDTO {

    private String account;

    private BigDecimal balance;

    private String orderNo;

    private String name;

    private BigDecimal orderAmount;

    private String createTime;

}
