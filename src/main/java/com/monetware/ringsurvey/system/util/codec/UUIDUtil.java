package com.monetware.ringsurvey.system.util.codec;

import com.monetware.ringsurvey.system.util.date.DateUtil;
import org.apache.commons.lang3.RandomUtils;

import java.util.Date;
import java.util.UUID;

public class UUIDUtil {
    /**
     * 获取java系统的uuid
     *
     * @return
     */
    public final static String getRandomUUID() {
        String uuid = "";
        synchronized (uuid) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    /**
     * 当前时间加上随机数的UUID
     *
     * @return
     */
    public final static String getTimestampUUID() {
        return DateUtil.formatDatetimeUUID(new Date()) + RandomUtils.nextInt(0, 100);
    }
}
