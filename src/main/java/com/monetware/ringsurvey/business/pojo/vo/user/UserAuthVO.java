package com.monetware.ringsurvey.business.pojo.vo.user;

import lombok.Data;

import java.util.List;

@Data
public class UserAuthVO {

    private Integer userId;

    private List<String> projectAuth;

}
