package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.library.LibraryDetailDTO;
import com.monetware.ringsurvey.business.pojo.dto.library.LibraryListDTO;
import com.monetware.ringsurvey.business.pojo.dto.library.QnaireShareDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseQuestionnaireLibrary;
import com.monetware.ringsurvey.business.pojo.vo.library.LibraryListVO;
import com.monetware.ringsurvey.business.pojo.vo.library.QnaireShareVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * @author Simo
 * @date 2019-02-27
 */
@Mapper
@Repository
public interface LibraryDao extends MyMapper<BaseQuestionnaireLibrary> {

    List<LibraryListDTO> getLibraryList(LibraryListVO listVO);

    LibraryDetailDTO getLibraryDetail(@Param("id") Integer id, @Param("userId") Integer userId);

    List<QnaireShareDTO> getMyShareList(QnaireShareVO shareVO);

}
