package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/25 11:57
 */
@Data
public class ResponseFileListVO extends PageParam {

    private String responseGuid;

    private Integer type;

    private String keyword;

    private Integer userId;

}
