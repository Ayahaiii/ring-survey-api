package com.monetware.ringsurvey.business.pojo.dto.library;

import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaireComments;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2019/11/25 15:05
 */
@Data
public class QnaireCommentsDTO extends BaseQuestionnaireComments {

    /**
     * 评论者名称
     */
    private String userName;


    /**
     * 被评论者名称
     */
    private String commentedName;


    /**
     * 子评论
     */
    private List<QnaireCommentsDTO> children;
}
