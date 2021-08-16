package com.monetware.ringsurvey.business.pojo.vo.issue;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

/**
 * @author Simo
 * @date 2020-04-08
 */
@Data
public class WxShareVO extends BaseVO {

    private String code;

    private String title;

    private String desc;

    private String imgUrl;

}
