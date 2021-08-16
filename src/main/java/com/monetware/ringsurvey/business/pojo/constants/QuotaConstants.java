package com.monetware.ringsurvey.business.pojo.constants;

/**
 * 配额有关常量
 */
public class QuotaConstants {

    /**
     * 样本池每次导入数量
     */
    public static final Integer POOL_IMPORT_LENGTH = 1000;

    /**
     * 初始化样本导入数量
     */
    public static final Integer INIT_IMPORT_LENGTH = 1000;

    /**
     * 配额状态
     * 0-禁用 1-启用
     */
    public static final Integer FORBIDDEN = 0;
    public static final Integer ENABLE = 1;


    /**
     * 配额类型
     * 1:问卷配额 2:样本配额 3:问卷配额+样本配额
     */
    public static final Integer QUESTIONNAIRE_QUOTA = 1;
    public static final Integer SAMPLE_QUOTA = 2;
    public static final Integer BOTH_QUOTA = 3;

    /**
     * 样本分配方式
     * 0-关闭 1-预先 2-动态
     */
    public static final Integer ASSIGN_TYPE_CLOSE = 0;
    public static final Integer ASSIGN_TYPE_STATIC = 1;
    public static final Integer ASSIGN_TYPE_DYNAMIC = 2;

}
