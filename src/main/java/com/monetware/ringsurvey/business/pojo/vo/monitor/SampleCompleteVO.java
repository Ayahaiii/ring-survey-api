package com.monetware.ringsurvey.business.pojo.vo.monitor;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @Author: lu
 * @Date: 2020/04/01 11:10
 * @Description: 计算访员绩效传入vo
 **/
@Data
public class SampleCompleteVO {

    /**
     * 项目id
     */
    private Integer projectId;

    /**
     * 时间区间
     * H 按小时
     * D 按天
     */
    private String type;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 访员id
     * @return
     */
    private Integer userId;

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
