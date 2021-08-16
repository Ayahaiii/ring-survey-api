package com.monetware.ringsurvey.business.pojo.dto.user;


import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/01 14:31
 * @Description: 用户id 和用户名集合
 **/
@Data
public class UserIdAndNameDTO {
    private Integer id;

    private String name;
}
