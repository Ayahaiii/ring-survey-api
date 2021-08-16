package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;

/**
 * 问卷资源标签DTO
 */
@Data
public class QnaireResourceTagListDTO {

    private Integer id;

    private String name;

    private Integer resourceCount;// 资源数量

}
