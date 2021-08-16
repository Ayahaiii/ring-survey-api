package com.monetware.ringsurvey.business.pojo.vo.analyzer;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-03-31
 */
@Data
public class AnalysisModuleVO extends BaseVO {

    private Integer id;

    private String name;

    private String type;

    private String baseSearch;

}
