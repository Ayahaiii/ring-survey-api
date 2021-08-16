package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;

/**
 * @author Simo
 * @date 2020-04-20
 */
@Data
public class ProjectModuleDTO {

    private Integer moduleId;

    private String name;

    private Integer qnaireId;

    private Integer status;

    private String code;

    private Integer version;

    private String jsDir;

}
