package com.monetware.ringsurvey.business.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "rs_project_export_history")
public class BaseProjectExportHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private Integer projectId;

  /**
   *  SAMPLE、TEAM、ITW、ITWT
   */
  private String type;

  private String name;

  private String description;

  private Long fileSize;

  private String fileType;

  private String filePath;

  private Integer createUser;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

}
