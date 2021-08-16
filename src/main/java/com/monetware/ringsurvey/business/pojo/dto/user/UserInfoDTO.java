package com.monetware.ringsurvey.business.pojo.dto.user;

import com.monetware.ringsurvey.business.pojo.po.BaseUser;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO extends BaseUser {

    /**
     * 生日(年
     */
    private String year;

    /**
     * 生日(月
     */
    private String month;

    private List<String> authList;

}
