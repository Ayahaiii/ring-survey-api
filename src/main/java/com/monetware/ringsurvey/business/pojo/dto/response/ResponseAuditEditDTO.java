package com.monetware.ringsurvey.business.pojo.dto.response;

import com.monetware.ringsurvey.business.pojo.po.BaseResponse;
import lombok.Data;

/**
 * 答卷审核编辑
 */
@Data
public class ResponseAuditEditDTO {

    private String name;

    private String submitData;

    private String submitState;

    private String responseData;

    private String questionData;

    private String jsDir;

}
