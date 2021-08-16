package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-26
 */
@Data
public class SampleMoreContactVO {

    private Integer id;

    private Integer projectId;

    private String name;

    /**
     * 关系
     */
    private String relation;

    /**
     * 备注
     */
    private String description;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话号码
     */
    private String mobile;

    /**
     * 手机号码
     */
    private String phone;

    private String weixin;

    private String qq;

    private String weibo;

}
