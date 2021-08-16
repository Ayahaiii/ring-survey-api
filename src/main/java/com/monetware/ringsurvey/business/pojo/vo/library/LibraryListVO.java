package com.monetware.ringsurvey.business.pojo.vo.library;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Linked
 * @date 2020/5/27 16:34
 */
@Data
public class LibraryListVO extends PageParam {

    private String keyword;
    /**
     * 1:系统提供 2:用户提供
     */
    private Integer ifUser;

    private BigDecimal height;

    private BigDecimal low;

    /**
     * 判断是否购买
     */
    private Integer ifBuy;

    private List<Integer> orderIds;// 废弃

    /**
     * 我发布的
     */
    private Integer mine;

    /**
     * 收藏
     */
    private Integer star;

    private String priceSort;

    private Integer userId;
    /**
     * 全部
     */
    private Integer all;

}
