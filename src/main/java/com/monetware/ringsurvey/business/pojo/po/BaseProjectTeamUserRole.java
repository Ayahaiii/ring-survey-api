package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Linked
 * @date 2020/2/17 17:31
 */
@Data
@Table(name = "rs_project_team_user_role")
public class BaseProjectTeamUserRole {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer projectId;

  private Integer roleId;

  private Integer userId;

}
