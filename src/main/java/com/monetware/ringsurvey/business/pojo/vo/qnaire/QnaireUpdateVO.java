package com.monetware.ringsurvey.business.pojo.vo.qnaire;

import lombok.Data;

@Data
public class QnaireUpdateVO {

    private Integer projectId;

    private Integer moduleId;

    private Integer status;

    private Integer ifNewVersion;

    private String remark;

}
