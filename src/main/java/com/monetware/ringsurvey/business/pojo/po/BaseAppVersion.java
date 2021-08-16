package com.monetware.ringsurvey.business.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "rs_app_version")
public class BaseAppVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer versionCode;

    private String versionName;

    private String versionDesc;

    private String versionUrl;

    /**
     * 是否强制更新
     */
    private Integer isMust;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifyTime;


}
