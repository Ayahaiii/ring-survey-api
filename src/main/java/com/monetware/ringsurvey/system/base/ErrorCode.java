package com.monetware.ringsurvey.system.base;


public enum ErrorCode {

    ROLE_WITHOUT(1000, "您没有操作权限"),
    CP_WITHOUT(1001, "您暂未启用当前功能"),

    //Token验证
    TOKEN_WITHOUT(10001, "无效的凭证"),
    TOKEN_INVALID(10002, "TOKEN失效"),

    UPLOAD_FAIL(50004, "上传失败"),
    //用户相关
    EMAIL_HAVE_BEEN_USED(20001, "邮箱已被占用"),
    PHONE_HAVE_BEEN_USED(20002, "手机已被占用"),
    NAME_HAVE_BEEN_USED(20003, "昵称已被占用"),
    SMS_CODE_SEND_FAIL(20004, "发送短信验证码失败"),
    SMS_CODE_FAILURE(20005, "验证码已过期"),
    SMS_CODE_WRONG(20006, "验证码错误"),
    USER_LOGIN_FAIL(20007, "登录错误，请输入正确账号密码"),
    EMAIL_SEND_FAIL(20008, "邮件发送失败"),
    OLD_PASSWORD_WRONG(20009, "旧密码错误"),

    //自定义错误
    CUSTOM_MSG(10000, ""),

    //项目
    PROJECT_NOT_EXIST(80001, "项目不存在"),
    PROJECT_WAITED(80002, "项目未启动"),
    PROJECT_PAUSED(80003, "项目已结束"),
    PROJECT_FINISHED(80004, "项目已归档"),
    PROJECT_CONFIG_ERROR(80005, "项目配置参数错误"),

    //样本
    SAMPLE_NOT_EXIST(70001, "样本不存在"),
    SAVE_SAMPLE_ERROR(70002, "样本保存失败"),
    SAMPLE_ASSIGN_OVER_ERROR(70003, "样本已分配完毕"),

    //答卷
    RESPONSE_OUT_TOP_LIMIT(90000, "答卷数量已达系统上限"),
    RESPONSE_NOT_EXIST(90001, "未找到答卷数据"),
    PARSE_RESPONSE_DATA_ERROR(90002, "提交数据解析失败"),
    SAVE_RESPONSE_DATA_ERROR(90003, "答卷保存失败"),
    SAVE_RESPONSE_HISTORY_ERROR(90004, "答卷历史保存失败"),
    RESPONSE_ENOUGH(90005, "问卷答卷已满足"),
    RESPONSE_SAMPLE_ERROR(90006, "答卷样本参数错误"),
    BONUS_STATUS_UPDATE_ERROR(90007, "答卷抽奖状态更新失败"),
    ONE_IP_ONE_SUBMIT(90008, "单个IP只能提交一份答卷"),
    ERROR_TYPE_SUBMIT(90009, "答卷提交方式不被允许"),
    SAVE_RESPONSE_POSITION_ERROR(90010, "答卷定位保存失败"),
    SAVE_INTERVIEW_TRAVEL_ERROR(90011, "访员轨迹保存失败"),

    //余额、红包
    SEND_SUCCESS(40000, "发送成功"),
    AMOUNT_NOT_ARRIVE(40001, "余额不足"),
    PACKET_NUM_NOT_ENOUGH(40002, "红包发送完毕"),
    PACKET_NOT_HIT(40003, "很遗憾，未抽中红包！"),
    WECHAT_AUTHORIZE_FAIL(40004, "获取微信授权失败"),
    PACKET_NEED_AUDIT(40005, "红包发放成功，管理员等待审核"),
    RESPONSE_PACKET_ALREADY_GET(40006, "此答卷红包已领取"),
    OVER_SYSTEM_TOTAL_LIMIT(40007, "超过系统红包总数限制"),
    OVER_SYSTEM_DAY_LIMIT(40008, "超过系统红包每天总数限制"),
    OVER_USER_TOTAL_LIMIT(40009, "超过单个用户红包总数限制"),
    OVER_USER_DAY_LIMIT(40010, "超过单个用户红包每天总数限制"),
    PACKET_RECORD_NOT_EXIST(40011, "红包领取记录不存在"),
    PACKET_RECORD_SAVE_ERROR(40012, "红包领取记录保存失败"),
    PACKET_RECORD_AUDIT_ERROR(40013, "红包领取记录审核失败"),
    PACKET_ALREADY_DRAW(40014, "您已抽取过红包"),
    PACKET_CONFIG_NOT_EXIST(40015, "未找到相应的红包配置"),
    PACKET_RETURN_ERROR(40016, "红包金额返还失败"),
    PACKET_AREA_LIMIT_ERROR(40017, "很遗憾，所在地区未满足领取条件"),

    //OM设备
    EXT_IDLE(60001, "分机空闲"),
    EXT_BUSY(60002, "分机正忙"),
    EXT_OFFLINE(60003, "分机离线"),
    ;


    private String msg;
    private int code;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}

