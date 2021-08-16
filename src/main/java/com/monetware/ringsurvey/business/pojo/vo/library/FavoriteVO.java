package com.monetware.ringsurvey.business.pojo.vo.library;

import lombok.Data;

/**
 * @author Linked
 * @date 2020/6/1 20:06
 */
@Data
public class FavoriteVO {

    private Integer libraryId;

    /**
     * 收藏/取消收藏
     */
    private Integer starType;

}
