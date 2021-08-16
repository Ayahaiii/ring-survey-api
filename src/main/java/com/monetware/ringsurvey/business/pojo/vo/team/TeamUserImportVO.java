package com.monetware.ringsurvey.business.pojo.vo.team;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-02-28
 */
@Data
public class TeamUserImportVO {

    private Integer projectId;

    private List<TeamUserImportInfoVO> userInfos;


}
