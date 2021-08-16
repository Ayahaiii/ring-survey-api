package com.monetware.ringsurvey.business.pojo.vo.monitor;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-03-31
 */
@Data
public class SampleStatusUseVO {

    private Integer projectId;

    private String type;

    private Date startTime;

    private Date endTime;

    private Integer status;

    public Integer change(){
        if(!StringUtils.isEmpty(type) && type.equals("H")){
            //小时
            return  1;
        }else{
            //天
            return  2;
        }
    }

}
