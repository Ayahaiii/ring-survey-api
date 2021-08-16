package com.monetware.ringsurvey.business.pojo.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simo
 * @date 2020-03-02
 */
public class SampleConstants {

    /**
     * 样本数量
     */
    public static final Integer TOP_LIMIT = 100000;

    /**
     * 初始化值
     */
    public static final Integer INIT_VALUE = 0;

    /**
     * 禁用
     */
    public static final Integer STATUS_DISABLED = -1;

    /**
     * 初始化 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_INIT = 0;

    /**
     * 已分派 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_ASSIGN = 1;

    /**
     * 进行中 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_RUNNING = 2;

    /**
     * 已完成 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_FINISH = 3;

    /**
     * 拒访 caxi capi cati
     */
    public static final Integer STATUS_REFUSED = 4;

    /**
     * 甄别 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_IDENTIFY = 5;

    /**
     * 预约 caxi capi cati
     */
    public static final Integer STATUS_YUYUE = 6;

    /**
     * 无效号码/无法联系 caxi capi cati
     */
    public static final Integer STATUS_INVALID = 7;

    /**
     * 通话中 cati
     */
    public static final Integer STATUS_CALLING = 8;

    /**
     * 无人接听 cati
     */
    public static final Integer STATUS_NOONE = 9;

    /**
     * 审核无效 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_AUDIT_INVALID = 10;

    /**
     * 审核回退 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_AUDIT_BACK = 11;

    /**
     * 审核成功 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_AUDIT_SUCCESS = 12;

    /**
     * 配额溢出 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_QUOTA_ENOUGH = 13;

    /**
     * 配额不满足 caxi capi cati cawi cadi
     */
    public static final Integer STATUS_QUOTA_NOT_SATISFY = 14;

    /**
     * 负责人
     */
    public static final Integer ASSIGN_TYPE_HEAD = 1;

    /**
     * 协作者
     */
    public static final Integer ASSIGN_TYPE_AID = 2;

    /**
     * 实体样本
     */
    public static final Integer VIRTUAL_NO = 1;

    /**
     * 虚拟样本
     */
    public static final Integer VIRTUAL_YES = 2;

    /**
     * 状态KEY值--all
     */
    public static List<Integer> STATUS_KEYS = new ArrayList<>();
    /**
     * 状态KEY值-cati
     */
    public static List<Integer> CATI_STATUS_KEYS = new ArrayList<>();
    /**
     * 状态KEY值-capi
     */
    public static List<Integer> CAPI_STATUS_KEYS = new ArrayList<>();
    /**
     * 状态KEY值-cawi
     */
    public static List<Integer> CAWI_STATUS_KEYS = new ArrayList<>();
    /**
     * 状态KEY值-cadi
     */
    public static List<Integer> CADI_STATUS_KEYS = new ArrayList<>();
    /**
     * 状态KEY值-caxi
     */
    public static List<Integer> CAXI_STATUS_KEYS = new ArrayList<>();

    static {
        //ALL
        STATUS_KEYS.add(STATUS_INIT);
        STATUS_KEYS.add(STATUS_ASSIGN);
        STATUS_KEYS.add(STATUS_RUNNING);
        STATUS_KEYS.add(STATUS_FINISH);
        STATUS_KEYS.add(STATUS_REFUSED);
        STATUS_KEYS.add(STATUS_IDENTIFY);
        STATUS_KEYS.add(STATUS_YUYUE);
        STATUS_KEYS.add(STATUS_INVALID);
        STATUS_KEYS.add(STATUS_CALLING);
        STATUS_KEYS.add(STATUS_NOONE);
        STATUS_KEYS.add(STATUS_AUDIT_INVALID);
        STATUS_KEYS.add(STATUS_AUDIT_BACK);
        STATUS_KEYS.add(STATUS_AUDIT_SUCCESS);
        //CATI
        CATI_STATUS_KEYS.add(STATUS_INIT);
        CATI_STATUS_KEYS.add(STATUS_ASSIGN);
        CATI_STATUS_KEYS.add(STATUS_RUNNING);
        CATI_STATUS_KEYS.add(STATUS_FINISH);
        CATI_STATUS_KEYS.add(STATUS_REFUSED);
        CATI_STATUS_KEYS.add(STATUS_IDENTIFY);
        CATI_STATUS_KEYS.add(STATUS_YUYUE);
        CATI_STATUS_KEYS.add(STATUS_INVALID);
        CATI_STATUS_KEYS.add(STATUS_CALLING);
        CATI_STATUS_KEYS.add(STATUS_NOONE);
        CATI_STATUS_KEYS.add(STATUS_AUDIT_INVALID);
        CATI_STATUS_KEYS.add(STATUS_AUDIT_BACK);
        CATI_STATUS_KEYS.add(STATUS_AUDIT_SUCCESS);
        //CAPI
        CAPI_STATUS_KEYS.add(STATUS_INIT);
        CAPI_STATUS_KEYS.add(STATUS_ASSIGN);
        CAPI_STATUS_KEYS.add(STATUS_RUNNING);
        CAPI_STATUS_KEYS.add(STATUS_FINISH);
        CAPI_STATUS_KEYS.add(STATUS_REFUSED);
        CAPI_STATUS_KEYS.add(STATUS_IDENTIFY);
        CAPI_STATUS_KEYS.add(STATUS_YUYUE);
        CAPI_STATUS_KEYS.add(STATUS_INVALID);
        CAPI_STATUS_KEYS.add(STATUS_AUDIT_INVALID);
        CAPI_STATUS_KEYS.add(STATUS_AUDIT_BACK);
        CAPI_STATUS_KEYS.add(STATUS_AUDIT_SUCCESS);
        //CAWI
        CAWI_STATUS_KEYS.add(STATUS_INIT);
        CAWI_STATUS_KEYS.add(STATUS_ASSIGN);
        CAWI_STATUS_KEYS.add(STATUS_RUNNING);
        CAWI_STATUS_KEYS.add(STATUS_FINISH);
        CAWI_STATUS_KEYS.add(STATUS_IDENTIFY);
        CAWI_STATUS_KEYS.add(STATUS_AUDIT_INVALID);
        CAWI_STATUS_KEYS.add(STATUS_AUDIT_BACK);
        CAWI_STATUS_KEYS.add(STATUS_AUDIT_SUCCESS);
        //CADI
        CADI_STATUS_KEYS.add(STATUS_INIT);
        CADI_STATUS_KEYS.add(STATUS_ASSIGN);
        CADI_STATUS_KEYS.add(STATUS_RUNNING);
        CADI_STATUS_KEYS.add(STATUS_FINISH);
        CADI_STATUS_KEYS.add(STATUS_IDENTIFY);
        CADI_STATUS_KEYS.add(STATUS_AUDIT_INVALID);
        CADI_STATUS_KEYS.add(STATUS_AUDIT_BACK);
        CADI_STATUS_KEYS.add(STATUS_AUDIT_SUCCESS);
        //CAXI
        CAXI_STATUS_KEYS.add(STATUS_INIT);
        CAXI_STATUS_KEYS.add(STATUS_ASSIGN);
        CAXI_STATUS_KEYS.add(STATUS_RUNNING);
        CAXI_STATUS_KEYS.add(STATUS_FINISH);
        CAXI_STATUS_KEYS.add(STATUS_REFUSED);
        CAXI_STATUS_KEYS.add(STATUS_IDENTIFY);
        CAXI_STATUS_KEYS.add(STATUS_YUYUE);
        CAXI_STATUS_KEYS.add(STATUS_INVALID);
        CAXI_STATUS_KEYS.add(STATUS_AUDIT_INVALID);
        CAXI_STATUS_KEYS.add(STATUS_AUDIT_BACK);
        CAXI_STATUS_KEYS.add(STATUS_AUDIT_SUCCESS);
    }

}
