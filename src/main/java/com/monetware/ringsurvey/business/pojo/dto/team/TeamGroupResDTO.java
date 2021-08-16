package com.monetware.ringsurvey.business.pojo.dto.team;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Simo
 * @date 2020-02-26
 */
@Data
public class TeamGroupResDTO {

    private List<TeamGroupDTO> treeGroup;

    private Map<Integer, Integer> treeValue;

}
