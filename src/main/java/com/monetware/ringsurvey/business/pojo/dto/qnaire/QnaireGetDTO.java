package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaire;
import lombok.Data;

@Data
public class QnaireGetDTO extends BaseQuestionnaire {

    private Integer moduleId;

}
