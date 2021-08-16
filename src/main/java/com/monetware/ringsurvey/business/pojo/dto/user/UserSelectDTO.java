package com.monetware.ringsurvey.business.pojo.dto.user;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Simo
 * @date 2019-10-11
 */
@Data
public class UserSelectDTO {

    private Integer id;

    private String name;

    private String telephone;

    private String email;

    private Integer role;

    private Date expireTime;

    private Integer status;

    private Date lastLoginTime;

    private String projectAuth;

    private List<String> authList;

}
