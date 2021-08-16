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
@Table(name = "rs_project_team_group")
public class BaseProjectTeamGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer projectId;

  private String name;

  /**
   * 父分组id
   */
  private Integer parentId;

  private Integer createUser;

  private Date createTime;

}
