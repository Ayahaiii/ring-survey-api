package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/4/1 16:23
 */
@Data
@Table(name = "rs_sample_base")
public class BaseSampleBase {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

    private String description;

    /**
     * 样本库类型:1-标准库 2-空号库
     */
    private Integer type;

    private Date createTime;

    private Integer createUser;

    private Date lastModifyTime;

    private Integer lastModifyUser;

    /**
     * 0:没删除 1:已删除
     */
    private Integer isDelete;

    private Integer deleteUser;

    private Date deleteTime;

}
