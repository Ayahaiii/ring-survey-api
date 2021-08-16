package com.monetware.ringsurvey.business.pojo.vo.team;

import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-28
 */
@Data
public class TeamUserImportInfoVO {

    private String userName;

    private String telephone;

    private String email;

    private Date authDate;

}
