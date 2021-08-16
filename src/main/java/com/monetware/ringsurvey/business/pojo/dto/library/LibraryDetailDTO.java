package com.monetware.ringsurvey.business.pojo.dto.library;

import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaireLibrary;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author Linked
 * @date 2020/5/27 19:21
 */
@Data
public class LibraryDetailDTO extends BaseQuestionnaireLibrary {

    private String createUserName;

    private Integer qnaireNum;

    private Integer ifComment;

    private Integer ifBuy;

    private Integer ifStar;

    private Integer buyCount;

}
