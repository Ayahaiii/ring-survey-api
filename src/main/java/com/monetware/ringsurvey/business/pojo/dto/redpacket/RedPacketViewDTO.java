package com.monetware.ringsurvey.business.pojo.dto.redpacket;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Simo
 * @date 2020-04-08
 */
@Data
public class RedPacketViewDTO {

    private BigDecimal balance;

    private Integer status;

}
