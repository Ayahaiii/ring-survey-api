package com.monetware.ringsurvey.business.pojo.dto.response;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 答卷导出DTO
 */
@Data
public class ResponseExportDTO {

    private Integer moduleId;

    private String moduleName;

    private Integer version;

    private Integer questionnaireId;

    private String xmlContent;

    private LinkedHashMap<String, String> titleMap;// 标题

    private Map<String, String> titleTypeMap;// 标题类型

    private Map<String, List<String>> optionMap;// 选项

    private List<ResponseExportSelectDTO> exportResponseList;

}
