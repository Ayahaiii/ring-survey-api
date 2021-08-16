package com.monetware.ringsurvey.business.pojo.po;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "rs_permission")
public class BasePermission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer role;

  private String surveyType;

  private Integer freeNum;

  private Integer qNum;

  private Integer dataStoreTime;

  private Integer qDesignVersion;

  private Integer teamNum;

  private Integer ifBonus;

  private Integer ifLogo;

  private Integer ifQMultiple;

  private Integer ifQuota;

  private Integer ifInterviewer;

  private Integer ifSampleExpand;

  private Integer ifHistory;

  private Integer ifDeploy;

  private Double price;


}
