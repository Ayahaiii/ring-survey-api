package com.monetware.ringsurvey.business.pojo.vo.analyzer;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/5/13 10:17
 */
@Data
public class BaseCodeVO {

    private String key;

    private List<List<String>> imgStrs;

}
