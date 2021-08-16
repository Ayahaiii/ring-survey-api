package com.monetware.ringsurvey.business.pojo.vo.team;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-26
 */
@Data
public class TeamUserDelVO {

    private Integer projectId;

    private List<Integer> userIds;

}
