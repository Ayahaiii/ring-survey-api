package com.monetware.ringsurvey.business.pojo.vo.response;

import com.monetware.ringsurvey.business.pojo.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * @author Linked
 * @date 2020/4/2 18:41
 */
@Data
public class DeleteFileVO extends BaseVO {

    private Integer type;

    private List<Integer> fileIds;

}
