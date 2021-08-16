package com.monetware.ringsurvey.business.pojo.vo.issue;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 短信、邮件配置VO
 */
@Data
public class PublishConfigVO extends BaseVO {

    private Integer id;

    private Integer type;// 1-短信 2-邮件

    private BigDecimal amount;

    private Integer num;// 购买条数

    private String subject;

    private String content;

    private Date sendTime;

    private Integer sendNum;

}
