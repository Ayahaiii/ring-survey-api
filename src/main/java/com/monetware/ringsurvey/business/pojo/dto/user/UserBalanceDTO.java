package com.monetware.ringsurvey.business.pojo.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Lin
 * @date 2019/10/24 17:26
 */
@Data
public class UserBalanceDTO {

    private BigDecimal balance;

    private BigDecimal useBalance;
}
