package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/4/8 10:43
 */
@Data
@Table(name = "rs_sample_base_property")
public class BaseSampleBaseProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sampleBaseId;

    private String name;

    private String code;

    /**
     * 字段类型：1.int；2.bigint；3.decimal；4.varchar；5.text'
     */
    private Integer type;

    /**
     * 字段存储数据的默认长度，除varchar外都不需要设置长度
     */
    private Integer length;

    /**
     * 展示顺序
     */
    private Integer serialNo;

    private Date createTime;

    private Integer createUser;

    private Date lastModifyTime;

    private Integer lastModifyUser;
}
