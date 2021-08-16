package com.monetware.ringsurvey.business.pojo.vo.quota;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/4/2 20:18
 */
@Data
public class QuotaDeleteVO extends BaseVO {

    private List<Integer> quotaIds;

}
