package com.monetware.ringsurvey.business.pojo.vo.team;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamGroupDelVO {

    private Integer projectId;

    private List<Integer> ids;

}
