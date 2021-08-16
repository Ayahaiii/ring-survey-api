package com.monetware.ringsurvey.business.pojo.dto.project;

import lombok.Data;

import java.util.List;

/**
 * 配置DTO（本地部署使用）
 */
@Data
public class ConfigDTO {

    private Integer needRegister;

    private Integer needAudit;

    private List<String> projectAuth;

}
