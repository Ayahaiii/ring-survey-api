package com.monetware.ringsurvey.business.pojo.constants;

/**
 * 电话调查常用参数
 */
public class CatiConstants {

    public static final String STANDARD_XML_HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";

    public static final String EXT = "ext";
    public static final String EXT_STATE = "state";
    public static final String EXT_FINISH = "FINISH";
    public static final String OUTER = "outer";
    public static final String TRUNK = "trunk";
    public static final String EVENT = "Event";
    public static final String RECORDING = "recording";

    /**
     * Cdr话单
     */
    public static final String CDR = "Cdr";
    public static final String CDR_CPN = "CPN";
    public static final String CDR_CDPN = "CDPN";
    public static final String CDR_DURATION = "Duration";
    public static final String CDR_RECORDING = "Recording";


    /**
     * 分机的线路状态
     * Ready: 空闲可用
     * Active: 振铃、回铃或通话中
     * Progress：模拟分机摘机后等待拨号以及拨号过程中
     * Offline: IP分机离线
     * Offhook：模拟分机听催挂音时的状态
     */
    public static final String EXT_READY = "Ready";
    public static final String EXT_ACTIVE = "Active";
    public static final String EXT_PROGRESS = "Progress";
    public static final String EXT_OFFLINE = "Offline";
    public static final String EXT_OFFHOOK = "Offhook";

    /**
     * 中继的线路状态
     * 可用(ready) 摘机、振铃或通话中(active) 未接线(unwired) 离线(offline)
     */
    public static final String TRUNK_READY = "ready";
    public static final String TRUNK_ACTIVE = "active";
    public static final String TRUNK_UNWIRED = "unwired";
    public static final String TRUNK_OFFLINE = "offline";

    /**
     * 通话状态
     * Talk: 通话进行中
     * Progress: 呼叫处理过程中
     * Wait: 呼叫等待中
     */
    public static final String CALL_STATUS_TALK = "Talk";
    public static final String CALL_STATUS_PROGRESS = "Progress";
    public static final String CALL_STATUS_WAIT = "Wait";

    /**
     * 事件报告分机状态
     * 空闲(IDLE)、忙线(BUSY)、上线(ONLINE)、离线(OFFLINE)
     */
    public static final String EVENT_IDLE = "IDLE";
    public static final String EVENT_BUSY = "BUSY";
    public static final String EVENT_ONLINE = "ONLINE";
    public static final String EVENT_OFFLINE = "OFFLINE";

}
