package com.monetware.ringsurvey.business.pojo.dto.monitor;

import lombok.Data;

/**
 * @Author: lu
 * @Date: 2020/04/14 19:20
 * @Description: 获取问卷list
 **/
@Data
public class GetQuestionnaireListDTO {
    /**
     * 模块名称
     */
    private String name;

    /**
     * 问卷id
     */
    private Integer qnaireId;
}
