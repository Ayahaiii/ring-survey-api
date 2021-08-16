package com.monetware.ringsurvey.business.pojo.vo.samplebase;

import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/4/8 18:53
 */
@Data
public class BaseSampleDelVO {

    private Integer sampleBaseId;

    private List<Integer> ids;

}
