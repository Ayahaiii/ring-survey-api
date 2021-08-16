package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 拨号规则
 */
@Data
@Table(name = "rs_number_rule")
public class BaseNumberRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private String name;

    private String areaCode;

    private String phoneNumber;

    private String extNum;

    private Integer serialNo;

    private Integer createUser;

    private Date createTime;

    private Integer lastModifyUser;

    private Date lastModifyTime;

    private Integer isDelete;

    private Integer deleteUser;

    private Date deleteTime;

}
