package com.monetware.ringsurvey.business.pojo.vo.team;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-28
 */
@Data
public class TeamUserExportVO {

    private Integer projectId;

    private String opt;

    private TeamUserSearchVO searchVO;

    private List<Integer> teamUserIds;

    private List<String>  properties;

    private String fileType;

    private String description;

}
