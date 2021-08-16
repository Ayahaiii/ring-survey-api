package com.monetware.ringsurvey.business.pojo.vo.sample;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;



/**
 * @author Linked
 * @date 2020/2/18 13:40
 */
@Data
public class ProjectSampleVO extends BaseVO {

    private Integer id;

    private String name;

    /**
     * 编号
     */
    private String code;

    private String gender;

    private Integer age;

    /**
     * 出生日期
     */
    private String birth;

    /**
     * 婚姻状况
     */
    private String marriageStatus;

    /**
     * 学历
     */
    private String education;

    /**
     * 单位
     */
    private String organization;

    /**
     * 职业
     */
    private String profession;

    /**
     * 职务
     */
    private String position;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 宗教
     */
    private String religion;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 语言
     */
    private String language;

    /**
     * 籍贯
     */
    private String placeOfBirth;

    /**
     * 方言
     */
    private String dialects;

    /**
     * 备注
     */
    private String description;

    /**
     * 详细介绍
     */
    private String detail;

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

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 镇
     */
    private String town;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 自定义
     */
    private String custom1;

    private String custom2;

    private String custom3;

    private String custom4;

    private String custom5;

}
