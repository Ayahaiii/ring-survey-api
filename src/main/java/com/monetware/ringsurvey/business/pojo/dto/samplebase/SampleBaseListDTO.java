package com.monetware.ringsurvey.business.pojo.dto.samplebase;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Linked
 * @date 2020/4/1 17:08
 */
@Data
public class SampleBaseListDTO {

    private Integer id;

    private Integer name;

    private Integer type;

    private Integer sampleCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
