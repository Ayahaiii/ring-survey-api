package com.monetware.ringsurvey.business.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rs_project")
public class BaseProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请码过期时间
     */
    private Date codeExpireTime;

    /**
     * 邀请自动审核
     */
    private Integer codeAutoAudit;

    /**
     * 项目发布码
     */
    private String publishCode;

    /**
     *
     * 团队数量
     */
    private Integer numOfTeam;

    /**
     *
     * 研究对象数量
     */
    private Integer numOfSample;

    /**
     * 信息量
     */
    private Long fileSize;

    /**
     *
     * 答卷数量
     */
    private Integer numOfAnswer;

    /**
     * 答卷总时长
     */
    private Long answerTimeLen;

    /**
     * 项目标签
     */
    private String labelText;

    /**
     * 项目类型ID
     */
    private String type;

    /**
     * 项目配置
     */
    private String config;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /**
     * 暂停时间
     */
    private Date pauseTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建用户
     */
    private Integer createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改用户
     */
    private Integer lastModifyUser;

    /**
     * 最后修改时间
     */
    private Date lastModifyTime;

    /**
     * 删除标记
     */
    private Integer isDelete;

    /**
     * 删除用户
     */
    private Integer deleteUser;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;

}
