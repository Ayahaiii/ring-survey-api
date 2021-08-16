package com.monetware.ringsurvey.business.pojo.dto.quota;

import lombok.Data;


/**
 * @author Linked
 * @date 2020/3/25 13:56
 */
@Data
public class QuotaModuleListDTO {

    private Integer moduleId;

    private String moduleName;

    private Integer questionnaireId;

    private String questionnaireName;

}
