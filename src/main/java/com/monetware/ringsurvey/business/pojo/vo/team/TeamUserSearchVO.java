package com.monetware.ringsurvey.business.pojo.vo.team;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamUserSearchVO extends PageParam {

    private String role;

    private Integer status;

    private String group;

    private Integer sampleAuth;

    private String keyword;

    private Integer userId;

}
