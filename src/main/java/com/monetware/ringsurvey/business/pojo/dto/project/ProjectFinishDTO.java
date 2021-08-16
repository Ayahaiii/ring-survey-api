package com.monetware.ringsurvey.business.pojo.dto.project;

import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-04-10
 */
@Data
public class ProjectFinishDTO {

    private Integer id;

    private Date createTime;

    private Date endDate;

    private Integer role;

    private Date userExpireTime;

}
