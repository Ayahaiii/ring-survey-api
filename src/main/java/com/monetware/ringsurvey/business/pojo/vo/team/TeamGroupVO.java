package com.monetware.ringsurvey.business.pojo.vo.team;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamGroupVO {

    private Integer id;

    private String name;

    private Integer parentId;

    private Integer projectId;
}
