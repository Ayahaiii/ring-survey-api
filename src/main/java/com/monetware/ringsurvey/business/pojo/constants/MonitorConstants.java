package com.monetware.ringsurvey.business.pojo.constants;

/**
 * @Author: lu
 * @Date: 2020/04/10 16:00
 * @Description: 监控相关常量
 **/
public class MonitorConstants {

    //============================================ 5种调查的状态数组 Begin =======================================
    /**
     * CATI STATUS INFO
     */
    public static String[] CATI_STATUS_INFO = {"样本总数","初始化","已分派","进行中","已完成","拒访","甄别","预约",
            "无效号码/无法联系","通话中","无人接听","审核无效","审核退回","审核成功"};

    /**
     * CAPI STATUS INFO
     */
    public static String[] CAPI_STATUS_INFO = {"样本总数","初始化","已分派","进行中","已完成","拒访","甄别","预约",
            "无效号码/无法联系","审核无效","审核退回","审核成功"};

    /**
     * CAWI STATUS INFO
     */
    public static String[] CAWI_STATUS_INFO = {"样本总数","初始化","已分派","进行中","已完成","甄别","审核无效","审核退回",
            "审核成功"};

    /**
     * CADI STATUS INFO
     */
    public static String[] CADI_STATUS_INFO = {"样本总数","初始化","已分派","进行中","已完成","甄别","审核无效","审核退回",
            "审核成功"};

    /**
     * CAXI STATUS INFO
     */
    public static String[] CAXI_STATUS_INFO = {"样本总数","初始化","已分派","进行中","已完成","拒访","甄别","预约",
            "无效号码/无法联系","审核无效","审核退回","审核成功"};
    //============================================ 5种调查的状态数组 End =========================================

    //============================================ 初始经纬度 Begin =======================================
    /**
     * lon
     */
    public static String LON = "106.403765";

    /**
     * lat
     */
    public static String LAT = "38.91";

    //============================================ 初始经纬度 End =========================================

}
