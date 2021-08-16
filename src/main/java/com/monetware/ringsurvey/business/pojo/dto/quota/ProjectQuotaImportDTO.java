package com.monetware.ringsurvey.business.pojo.dto.quota;

import com.monetware.ringsurvey.business.pojo.vo.quota.QuotaImportVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Linked
 * @date 2020/3/24 19:16
 */
@Data
public class ProjectQuotaImportDTO {

    private Map<String, List<QuotaImportVO>> resultMap;


}
