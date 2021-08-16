package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 项目样本状态变化记录
 */
@Data
@Table(name = "rs_project_sample_status_record")
public class BaseProjectSampleStatusRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer projectId;

    private String sampleGuid;

    private Integer status;

    private Date createTime;

    public BaseProjectSampleStatusRecord() {}

    public BaseProjectSampleStatusRecord(Integer projectId, String sampleGuid, Integer status, Date createTime) {
        this.projectId = projectId;
        this.sampleGuid = sampleGuid;
        this.status = status;
        this.createTime = createTime;
    }

}
