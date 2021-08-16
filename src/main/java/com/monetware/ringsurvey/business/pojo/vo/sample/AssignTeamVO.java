package com.monetware.ringsurvey.business.pojo.vo.sample;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/2/20 10:31
 */
@Data
public class AssignTeamVO {

    private Integer projectId;

    private String keyword;

    private String sampleCondition;


    /**
     * 研究对象
     */
    private List<String> sampleGuids;

    /**
     * 负责人
     */
    private Integer managerId;

    /**
     * 协助者
     */
    private List<Integer> assistantId;

    /**
     * ALL CHECK SELECT
     */
    private String opt;

}
