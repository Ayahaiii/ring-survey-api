package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Linked
 * @date 2020/4/8 11:00
 */
@Data
@Table(name = "rs_dictionary_sample_property")
public class BaseDictionarySampleProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段编码
     */
    private String code;

    /**
     * 字段类型：1.int；2.bigint；3.decimal；4.varchar；5.text
     */
    private Integer type;

    /**
     * 字段存储数据的默认长度，除varchar外都不需要设置长度
     */
    private Integer length;
}
