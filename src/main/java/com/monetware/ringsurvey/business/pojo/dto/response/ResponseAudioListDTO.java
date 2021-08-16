package com.monetware.ringsurvey.business.pojo.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/8/6 10:07
 */
@Data
public class ResponseAudioListDTO {

    public Integer id;

    public String audioName;

    public String duration;

    public String durationStr;

    public String size;

    public String filePath;

    public Date createTime;

    public String createTimeStr;

    public String interviewName;

}
