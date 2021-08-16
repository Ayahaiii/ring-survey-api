package com.monetware.ringsurvey.business.pojo.dto.monitor;

import com.github.pagehelper.Page;
import com.monetware.ringsurvey.system.base.PageList;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class SampleStatisticsDTO {

    private List<Integer> statusList;

    private PageList<Page> dataList;

}
