package com.monetware.ringsurvey.business.pojo.constants;

import com.monetware.threadlocal.ThreadLocalManager;

/**
 * @author Simo
 * @date 2020-03-18
 */
public class RedisKeyConstants {


    public static String baseKey = "RS_PROJECT_";


    public static String projectKey(Integer projectId) {
        return baseKey + projectId;
    }

    public static String projectRoleKey(Integer projectId) {
        return baseKey + "ROLE_" + projectId;
    }

    public static String projectConfigKey(Integer projectId) {
        return baseKey + "CONFIG_" + projectId;
    }

    public static String projectDataKey(Integer projectId) {
        return baseKey + "DATA_" + projectId;
    }

    public static String permissionKey(Integer projectId) {
        return "RS_AUTH_" + projectId + "_" + ThreadLocalManager.getUserId();
    }

    public static String pmRoleKey(Integer projectId) {
        return "RS_ROLE_" + projectId + "_" + ThreadLocalManager.getUserId();
    }

    public static String wxShareKey(String code) {
        return "RS_WX_SHARE_" + code;
    }

}
