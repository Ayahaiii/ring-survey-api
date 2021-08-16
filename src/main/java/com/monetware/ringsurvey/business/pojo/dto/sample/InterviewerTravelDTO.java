package com.monetware.ringsurvey.business.pojo.dto.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class InterviewerTravelDTO {

    private Integer userId;

    private String userName;

    private String lon;

    private String lat;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
