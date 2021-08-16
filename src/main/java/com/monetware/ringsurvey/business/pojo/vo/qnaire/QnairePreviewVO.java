package com.monetware.ringsurvey.business.pojo.vo.qnaire;

import lombok.Data;

/**
 * 问卷预览VO
 */
@Data
public class QnairePreviewVO {

    private Integer questionnaireId;

    private Integer moduleId;

    private Integer from;// 1-问卷中心 2-项目问卷

}
