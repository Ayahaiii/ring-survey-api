package com.monetware.ringsurvey.business.pojo.vo.questionnaire;

import com.monetware.ringsurvey.system.base.PageParam;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-25
 */
@Data
public class MyQnaireListVO extends PageParam {

    private String keyword;

    private String labelText;

    private Integer userId;

}
