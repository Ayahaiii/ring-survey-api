package com.monetware.ringsurvey.business.pojo.vo.library;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Linked
 * @date 2020/6/2 11:20
 */
@Data
public class QnaireShareVO {

    private Integer userId;

    private BigDecimal height;

    private BigDecimal low;

    private String keyword;

    private String priceSort;

}
