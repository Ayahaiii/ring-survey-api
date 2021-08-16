package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rs_user")
public class BaseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     * 1：男
     * 2：女
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 院校/公司
     */
    private String company;

    /**
     * 最高学历
     * 1：博士研究生
     * 2：本科生
     * 3：专科生
     */
    private Integer education;

    /**
     * 毕业院校
     */
    private String college;

    /**
     * 权限
     * 0：管理员
     * 1：普通用户
     * 2：VIP
     * 3：企业
     * 4：定制
     */
    private Integer role;

    /**
     * 权限过期时间
     */
    private Date expireTime;

    /**
     * 免费使用问卷模板数量
     */
    private Integer useFreeNum;

    /**
     * 状态
     * 0：禁用
     * 1：启用
     */
    private Integer status;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 项目权限（用于本地部署）
     */
    private String projectAuth;

    /**
     * 头像
     */
    private String avatarPath;

}
