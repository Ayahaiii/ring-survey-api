package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Table(name = "rs_project_publish_config")
public class BaseProjectPublishConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer type;// 1短信 2邮件

    private BigDecimal totalAmount;// 充值金额

    private Integer num;// 可用条数

    /**
     * 项目Id
     */
    private Integer projectId;

    /**
     * 主题
     */
    private String subject;

    /**
     * 正文
     */
    private String content;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 发送数量
     */
    private Integer sendNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

}
