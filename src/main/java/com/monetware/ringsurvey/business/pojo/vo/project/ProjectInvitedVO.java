package com.monetware.ringsurvey.business.pojo.vo.project;

import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-03-05
 */
@Data
public class ProjectInvitedVO {

    private Integer id;

    /**
     * 邀请码过期时间
     */
    private Date codeExpireTime;

    /**
     * 邀请自动审核
     */
    private Integer codeAutoAudit;

}
