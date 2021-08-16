package com.monetware.ringsurvey.business.pojo.vo;

import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.constants.RedisKeyConstants;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.ringsurvey.system.util.spring.SpringBeanUtil;

/**
 * @author Simo
 * @date 2020-03-03
 */
public class BaseVO {

    private Integer projectId;

    private Integer checkRole;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getCheckRole() {
        RedisUtil redisUtil = SpringBeanUtil.getBean(RedisUtil.class);
        String roleKey = RedisKeyConstants.pmRoleKey(this.projectId);
        if (redisUtil.hasKey(roleKey)) {
            String role = redisUtil.get(roleKey).toString();
            if (role.contains(AuthorizedConstants.ROLE_ADMIN.toString())) {
                return AuthorizedConstants.ROLE_ADMIN;
            } else if (role.contains(AuthorizedConstants.ROLE_SUPERVISOR.toString())){
                return AuthorizedConstants.ROLE_SUPERVISOR;
            } else {
                return AuthorizedConstants.ROLE_INTERVIEWER;
            }
        }
        return AuthorizedConstants.ROLE_INTERVIEWER;
    }

    public void setCheckRole(Integer checkRole) {
        this.checkRole = checkRole;
    }

}
