package com.monetware.ringsurvey.business.pojo.dto.team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamUserDTO {

    private Integer id;

    private Integer userId;

    private String name;

    private String userName;

    private String email;

    private String telephone;

    private Integer sampleNum;

    private String role;

    private String groupName;

    private String joinDate;

    private Integer sampleAuth;

    private Integer authType;

    private String validDate;

    private Integer status;
}
