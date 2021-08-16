package com.monetware.ringsurvey.business.pojo.vo.response;

import lombok.Data;

@Data
public class ResponseExportSearchVO extends ResponseListVO {

    private Integer projectId;

    private Integer sampleStatus;

    private Integer responseType;// 1-有效 2-无效

    private Integer qnaireVariable;// 问卷变量 0-否 1-是

    private Integer titleType;// 问卷标题 1-题号 2-题干

    private Integer optionType;// 选项标题 1-编号 2-题干

    private Integer multiSelectType;// 多选题 1-单列 2-逐列

    private Integer needNotes;// 批注 0-否 1-是

}
