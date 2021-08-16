package com.monetware.ringsurvey.business.pojo.dto.team;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamGroupDTO {

    private Integer id;

    private String label;

    private List<TeamGroupDTO> children;

}
