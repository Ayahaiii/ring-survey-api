package com.monetware.ringsurvey.business.pojo.dto.user;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-24
 */
@Data
public class UserPermissionDTO {

    private Integer role;

    private List<String> projectAuth;

}
