package com.monetware.ringsurvey.business.pojo.dto.user;

import com.monetware.ringsurvey.business.pojo.po.payOrder.BaseBuyRecord;
import lombok.Data;

/**
 * @author Lin
 * @date 2019/11/11 11:57
 */
@Data
public class BuyRecordDTO extends BaseBuyRecord {

    /**
     * 流水编号
     */
    private String outTradeNo;
}
