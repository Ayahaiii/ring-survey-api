package com.monetware.ringsurvey.business.pojo.dto.library;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Linked
 * @date 2020/5/27 17:46
 */
@Data
public class LibraryListDTO {

    private Integer id;

    private String name;

    private BigDecimal price;

    private Double rate;

    private String description;

    private String imageUrl;

    private String createUser;

    private Integer viewCount;// 浏览次数

    private Integer starCount;// 收藏次数

    private Integer buyCount;// 购买次数

    private Integer commentCount;// 评论次数

    private Integer ifBuy;

    private Integer ifFree;

    private Integer ifStar;

    private Integer status;
}
