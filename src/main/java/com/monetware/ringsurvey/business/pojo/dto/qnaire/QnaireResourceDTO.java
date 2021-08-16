package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.monetware.ringsurvey.business.pojo.po.BaseQnaireResource;
import lombok.Data;

import java.util.List;

/**
 * 问卷资源库DTO
 */
@Data
public class QnaireResourceDTO {

    private Integer tagId;// 标签Id

    private String tagName;// 标签名

    private Integer resourceCount;// 资源数量

    private List<BaseQnaireResource> resourceList;// 资源列表

}
