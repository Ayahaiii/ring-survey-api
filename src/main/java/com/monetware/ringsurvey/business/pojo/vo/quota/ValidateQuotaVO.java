package com.monetware.ringsurvey.business.pojo.vo.quota;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/3/25 16:56
 */
@Data
public class ValidateQuotaVO extends BaseVO {

    private Integer questionnaireId;

    private String quotaCode;

    private Integer type;

}
