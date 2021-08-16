package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-25
 */
@Data
public class ProjectModuleListDTO {

    private String name;

    private List<ProjectModuleQuestionnaireListDTO> modules;
}
