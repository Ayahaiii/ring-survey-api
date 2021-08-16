package com.monetware.ringsurvey.business.pojo.vo.user;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2019-10-11
 */
@Data
public class UserSelectVO extends PageParam {

    private String name;

    private String phone;

    private String email;

}
