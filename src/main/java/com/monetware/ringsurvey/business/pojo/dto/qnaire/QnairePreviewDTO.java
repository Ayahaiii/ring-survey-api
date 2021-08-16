package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaire;
import lombok.Data;

/**
 * 问卷预览DTO
 */
@Data
public class QnairePreviewDTO {

    private String name;

    private String logoUrl;

    private String bgUrl;

    private String jsDir;

}
