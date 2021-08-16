package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;


/**
 * 项目所有属性
 */
@Data
public class BaseProjectAllProperty {

    private String code;
    private String name;
    private String gender;
    private String organization;
    private String birth;
    private String age;
    private String mobile;
    private String weixin;
    private String phone;
    private String qq;
    private String email;
    private String weibo;
    private String province;
    private String city;
    private String district;
    private String town;
    private String address;
    private String marriageStatus;
    private String education;
    private String politicalStatus;
    private String nationality;
    private String position;
    private String placeOfBirth;
    private String religion;
    private String language;
    private String dialects;
    private String description;
    private String detail;
    private String recycleTimes;
    private String contactTimes;
    private String smsTimes;
    private String emailTimes;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
    private String custom5;
    private String managerName;
    private String lastModifyTime;

    public BaseProjectAllProperty() {
        this.code = "样本编码";
        this.name = "样本名称";
        this.gender = "性别";
        this.organization = "单位";
        this.birth = "出生日期";
        this.age = "年龄";
        this.mobile = "电话";
        this.weixin = "微信";
        this.phone = "手机";
        this.qq = "QQ";
        this.email = "邮箱";
        this.weibo = "微博";
        this.province = "省";
        this.city = "市";
        this.district = "县";
        this.town = "街道/镇";
        this.address = "详细地址";
        this.marriageStatus = "婚姻状况";
        this.education = "学历";
        this.politicalStatus = "政治面貌";
        this.nationality = "国籍";
        this.position = "职业";
        this.placeOfBirth = "籍贯";
        this.religion = "宗教信仰";
        this.language = "语言";
        this.dialects = "方言";
        this.description = "备注";
        this.detail = "详细介绍";
        this.recycleTimes = "回收次数";
        this.contactTimes = "接触次数";
        this.smsTimes = "短信发送次数";
        this.emailTimes = "邮件发送次数";
        this.custom1 = "自定义1";
        this.custom2 = "自定义2";
        this.custom3 = "自定义3";
        this.custom4 = "自定义4";
        this.custom5 = "自定义5";
        this.managerName = "负责人";
        this.lastModifyTime = "修改时间";
    }
}
