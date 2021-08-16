package com.monetware.ringsurvey.business.pojo.constants;

/**
 * 问卷市场常量
 */
public class LibraryConstants {

    public static final Integer PIC_SIZE = 100;// 图片压缩后大小
    public static final Float PIC_ACCURACY = 0.4f;// 图片压缩比例

    /**
     * 0-非市场 1-市场
     */
    public static final Integer LIBRARY_NO = 0;
    public static final Integer LIBRARY_YES = 1;

    /**
     * 状态
     * 0-初始化 1-准备中(审核中) 2-在售中 3-审核失败 4-下架
     */
    public static final Integer STATUS_INIT = 0;
    public static final Integer STATUS_PREPARING = 1;
    public static final Integer STATUS_MARKETING = 2;
    public static final Integer STATUS_AUDIT_FAIL = 3;
    public static final Integer STATUS_OFF = 4;

    /**
     * 类型
     * 1-购买 2-收藏
     */
    public static final Integer TYPE_BUY = 1;
    public static final Integer TYPE_COLLECT = 2;

}
