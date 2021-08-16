package com.monetware.ringsurvey.business.service;

import com.monetware.ringsurvey.system.util.codec.UUIDUtil;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Simo
 * @date 2020-03-03
 */
@Slf4j
@Service
public class LoginService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 开启二维码唯一标示
     * @return
     */
    public String setScanKey() {
        String scanKey = UUIDUtil.getRandomUUID();
        redisUtil.set(scanKey, "INIT", 2 * 60 * 60 * 1000l);
        return scanKey;
    }

    /**
     * 获取二维码扫描结果
     * @param scanKey
     * @return
     */
    public String getScanResult(String scanKey) {
        if (redisUtil.hasKey(scanKey)) {
            return redisUtil.get(scanKey).toString();
        }
        return "EXPIRE";
    }

    /**
     * 删除二维码标示
     * @param scanKey
     */
    public void deleteScanKey(String scanKey) {
        redisUtil.remove(scanKey);
    }

}
