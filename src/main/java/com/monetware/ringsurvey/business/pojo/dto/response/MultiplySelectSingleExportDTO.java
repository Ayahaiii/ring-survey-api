package com.monetware.ringsurvey.business.pojo.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 多选题单列导出DTO
 */
@Data
public class MultiplySelectSingleExportDTO {

    private String questionId;

    private String title;

    private String value;

    private List<String> inputTitleList;// 追加输入标题列表

    private List<String> inputValueList;// 追加输入值列表

}
