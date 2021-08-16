package com.monetware.ringsurvey.business.pojo.vo.quota;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/23 16:39
 */
@Data
public class BatchImportQuotaVO extends BaseVO {

    private List<QuotaImportVO> quotaList;

}
