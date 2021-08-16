package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.po.BaseResponseFile;
import com.monetware.ringsurvey.business.pojo.vo.response.ResponseFileListVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Linked
 * @date 2020/3/25 11:53
 */
@Mapper
@Repository
public interface ProjectResponseFileDao extends MyMapper<BaseResponseFile> {

    List<BaseResponseFile> getResponseFileList(ResponseFileListVO fileVO);

}
