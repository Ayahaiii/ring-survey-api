package com.monetware.ringsurvey.business.pojo.dto.library;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/6/2 11:14
 */
@Data
public class QnaireShareDTO {

    private Integer id;

    private String name;

    private BigDecimal price;

    private Integer viewCount;

    private Integer starCount;

    private Integer sales;

    private BigDecimal profit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


}
