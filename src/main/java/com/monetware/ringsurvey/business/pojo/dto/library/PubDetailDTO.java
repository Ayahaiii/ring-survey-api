package com.monetware.ringsurvey.business.pojo.dto.library;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Linked
 * @date 2020/5/28 10:43
 */
@Data
public class PubDetailDTO {

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Integer ifFree;

}
