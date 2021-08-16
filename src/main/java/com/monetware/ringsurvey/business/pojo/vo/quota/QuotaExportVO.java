package com.monetware.ringsurvey.business.pojo.vo.quota;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/23 13:52
 */
@Data
public class QuotaExportVO {

    private Integer projectId;

    private List<Integer> quotaIds;

}
