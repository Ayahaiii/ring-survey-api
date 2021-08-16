package com.monetware.ringsurvey.business.pojo.vo.monitor;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author Simo
 * @date 2020-03-24
 */
@Data
public class AnswerProcessVO extends BaseVO {

    private Integer userId;

    private Integer qnaireId;

    private String type;

    /**
     * 开始时间（格式：2020-01-01）
     */
    private Date startTime;

    /**
     * 结束时间（格式：2020-01-02）
     */
    private Date endTime;

    public Integer change() {
        if (!StringUtils.isEmpty(type) && type.equals("H")) {
            //小时
            return 1;
        } else {
            //天
            return 2;
        }
    }

}
