package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * 问题选择DTO(用于选择问题下拉框中)
 */
@Data
public class QuestionSelectedDTO {

    private Integer qid;

    private String title;

    List<HashMap> questions;


}
