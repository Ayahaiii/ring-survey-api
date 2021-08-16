package com.monetware.ringsurvey.business.pojo.vo.user;

import com.monetware.ringsurvey.business.pojo.po.BaseUser;
import lombok.Data;

/**
 * 用户修改VO
 */
@Data
public class UserUpdateVO extends BaseUser {

    /**
     * 生日(年
     */
    private String year;

    /**
     * 生日(月
     */
    private String month;

}
