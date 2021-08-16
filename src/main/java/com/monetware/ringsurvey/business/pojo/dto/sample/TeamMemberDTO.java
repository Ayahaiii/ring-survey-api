package com.monetware.ringsurvey.business.pojo.dto.sample;

import lombok.Data;

/**
 * @author Linked
 * @date 2020/2/20 14:50
 */
@Data
public class TeamMemberDTO {

    private Integer id;

    private Integer userId;

    private String roleId;

    private String memberName;

    private String authCondition;

}
