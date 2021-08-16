package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/17 17:31
 */
@Data
@Table(name = "rs_project_team_user")
public class BaseProjectTeamUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private Integer userId;

    private String userName;

    private String name;

    private String email;

    private String telephone;

    private Integer sampleAuth;

    private String authCondition;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 同意时间
     */
    private Date approveTime;

    /**
     * 同意人
     */
    private Integer approveUser;

    /**
     * 授权类型
     */
    private Integer authType;

    /**
     * 授权结束时间
     */
    private Date authEndTime;

    /**
     * 状态 1：待同意 2：有效 3：无效
     */
    private Integer status;


}
