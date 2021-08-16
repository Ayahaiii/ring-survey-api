package com.monetware.ringsurvey.business.pojo.po;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Linked
 * @date 2020/2/17 17:31
 */
@Data
@Table(name = "rs_project_property_template")
public class BaseProjectPropertyTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  /**
   * 所有属性
   */
  private String allProperty;

  /**
   * 列表使用属性
   */
  private String useProperty;

  /**
   * 列表显示属性
   */
  private String listProperty;

  private String markProperty;

  private Integer createUser;

  private Date createTime;


}
