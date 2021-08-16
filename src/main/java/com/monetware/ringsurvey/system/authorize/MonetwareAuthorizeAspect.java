package com.monetware.ringsurvey.system.authorize;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetware.ringsurvey.business.pojo.constants.AuthorizedConstants;
import com.monetware.ringsurvey.business.pojo.constants.ProjectConstants;
import com.monetware.ringsurvey.business.pojo.constants.RedisKeyConstants;
import com.monetware.ringsurvey.business.service.project.ProjectService;
import com.monetware.ringsurvey.survml.common.ProjectConfigDTO;
import com.monetware.ringsurvey.system.base.ErrorCode;
import com.monetware.ringsurvey.system.exception.ServiceException;
import com.monetware.ringsurvey.system.util.redis.RedisUtil;
import com.monetware.ringsurvey.system.util.spring.SpringBeanUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-20
 */
@Aspect
@Component
public class MonetwareAuthorizeAspect {


    @Pointcut("@annotation(monetwareAuthorize)")
    public void doAuthorize(MonetwareAuthorize monetwareAuthorize) {
    }


    @Around("doAuthorize(monetwareAuthorize)")
    public Object deBefore(ProceedingJoinPoint pjp, MonetwareAuthorize monetwareAuthorize) throws Throwable {

        // 获取当前项目ID
        String projectId;
        Object[] args = pjp.getArgs();
        Object obj = args[0];
        if (obj instanceof Integer) {
            // 获取restful上ID
            projectId = obj.toString();
        } else {
            // 获取json数据中ID
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject json = JSONObject.parseObject(objectMapper.writeValueAsString(args[0]));
            projectId = json.getString("projectId");
            if (projectId == null)  projectId = json.getString("id");
        }

        // 没有传递项目ID视为非法攻击
        if (projectId == null) throw new ServiceException(ErrorCode.ROLE_WITHOUT);

        RedisUtil redisUtil = SpringBeanUtil.getBean(RedisUtil.class);
        ProjectService projectService = SpringBeanUtil.getBean(ProjectService.class);
        // 验证接口开启权限
        // 判断是否有当前访问接口权限
        String cp = monetwareAuthorize.cp();
        if (!StringUtils.isEmpty(cp)) {
            String projectKey = RedisKeyConstants.projectKey(Integer.parseInt(projectId));
            String configKey = RedisKeyConstants.projectConfigKey(Integer.parseInt(projectId));
            ProjectConfigDTO configDTO = null;
            if (redisUtil.hexist(projectKey, configKey)) {
                configDTO = (ProjectConfigDTO) redisUtil.hget(projectKey, configKey);
            } else {
                configDTO = projectService.getProjectConfig(Integer.parseInt(projectId));
            }
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(configDTO);
            if (jsonObject.containsKey(cp) && ProjectConstants.CLOSE.equals(jsonObject.getInteger(cp))) {
                throw new ServiceException(ErrorCode.CP_WITHOUT);
            }
        }

        // 验证权限
        String authKey = RedisKeyConstants.permissionKey(Integer.parseInt(projectId));
        List<String> permissions = null;
        if (redisUtil.hasKey(authKey)) {
            permissions = (List<String>) redisUtil.get(authKey);
        } else {

            permissions = projectService.getProjectPermission(Integer.parseInt(projectId));
            redisUtil.set(authKey, permissions);
        }

        // 没有权限 即未分配权限
        if (permissions == null || permissions.size() == 0) throw new ServiceException(ErrorCode.ROLE_WITHOUT);

        // 判断是否拥有所有权限
        if (permissions.contains(AuthorizedConstants.R_ALL)) return pjp.proceed();

        String pm = monetwareAuthorize.pm();
        if (permissions.contains(pm))  {
            return pjp.proceed();
        } else {
            throw new ServiceException(ErrorCode.ROLE_WITHOUT);
        }

    }

}
