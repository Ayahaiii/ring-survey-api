package com.monetware.ringsurvey.business.pojo.vo.team;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-02-19
 */
@Data
public class TeamGroupUserSearchVO extends PageParam {

    private Integer projectId;

    private Integer groupId;

}
