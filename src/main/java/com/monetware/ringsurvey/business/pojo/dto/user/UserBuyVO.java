package com.monetware.ringsurvey.business.pojo.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Simo
 * @date 2019-09-18
 */
@Data
public class UserBuyVO {

    private BigDecimal orderAmount;

    private Integer num;

    private Integer type;

    private Integer projectId;

    private Integer libraryId;

    private Boolean useFree;

}
