package com.monetware.ringsurvey.business.pojo.dto.response;

import com.monetware.ringsurvey.business.pojo.dto.qnaire.QuestionSelectedDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseSampleAssignment;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/4/2 13:47
 */
@Data
public class SearchInfoDTO {

//    private Integer questionnaireId;
//
//    private String questionnaireName;
//
//    private Integer version;
//
//    private String auditUserName;
//
//    private Integer auditUserId;

    private List<QuestionSelectedDTO> questionSelectedList;

    private List<InterviewerDTO> interviewerList;


}
