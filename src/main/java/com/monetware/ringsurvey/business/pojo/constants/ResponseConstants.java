package com.monetware.ringsurvey.business.pojo.constants;

public class ResponseConstants {

    /**
     * 答卷上限
     */
    public static final Integer TOP_LIMIT = 100000;

    /**
     * 有效
     */
    public final static Integer RESPONSE_TYPE_VALID = 1;// 答卷有效
    public final static Integer RESPONSE_TYPE_INVALID = 2;// 答卷无效

    /**
     * 审核
     */
    public final static Integer AUDIT_RESULT_VALID = 1;//审核有效
    public final static Integer AUDIT_RESULT_INVALID = 2;//审核无效
    public final static Integer AUDIT_RESULT_MODIFY = 3;//审核-打回
    public final static Integer AUDIT_RESULT_VALID_ONE = 4;//一审合格
    public final static Integer AUDIT_RESULT_VALID_TWO = 5;//二审合格
    public final static Integer AUDIT_RESULT_VALID_THREE = 6;//终审合格
    public final static Integer AUDIT_RESULT_INVALID_ONE = 7;//一审不通过
    public final static Integer AUDIT_RESULT_INVALID_TWO = 8;//二审不通过
    public final static Integer AUDIT_RESULT_INVALID_THREE = 9;//三审不通过

    /**
     * 状态
     */
    public final static Integer RESPONSE_STATUS_START = 1;// 答题中
    public final static Integer RESPONSE_STATUS_CALLBACK = 2;// 预约回访
    public final static Integer RESPONSE_STATUS_REFUSE = 3;// 拒绝回访
    public final static Integer RESPONSE_STATUS_FAIL = 4;// 甄别失败
    public final static Integer RESPONSE_STATUS_QUOTA_NOT_SATISFIED = 5;// 不满足配额
    public final static Integer RESPONSE_STATUS_SUCCESS = 6;// 答题成功
    public final static Integer RESPONSE_STATUS_QUOTA_ENOUGH = 7;// 配额溢出
    public final static Integer RESPONSE_STATUS_AUDIT_FIRST_SUCCESS = 8;// 一审合格
    public final static Integer RESPONSE_STATUS_AUDIT_SECOND_SUCCESS = 9;// 二审合格
    public final static Integer RESPONSE_STATUS_AUDIT_THIRD_SUCCESS = 10;// 终审合格
    public final static Integer RESPONSE_STATUS_AUDIT_FAIL = 11;// 审核无效
    public final static Integer RESPONSE_STATUS_AUDIT_BACK = 12;// 打回

    /**
     * 答卷异常
     * 0-否 1-是
     */
    public final static Integer EXCEPTION_NO = 0;
    public final static Integer EXCEPTION_YES = 1;

    public final static String EXPORT_TYPE_EXCEL = "EXCEL";
    public final static String EXPORT_TYPE_CSV = "CSV";
    public final static String EXPORT_TYPE_SPSS = "SPSS";
    public final static String EXPORT_TYPE_DBF = "DBF";

    /**
     * 导出格式
     * 1-编号 2-内容
     */
    public final static Integer EXPORT_ID = 1;
    public final static Integer EXPORT_CONTENT = 2;

    /**
     * 多选题导出格式
     * 1-单列 2-逐列
     */
    public final static Integer EXPORT_SINGLE = 1;
    public final static Integer EXPORT_MULTIPLE = 2;

}
