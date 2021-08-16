package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.user.BuyRecordDTO;
import com.monetware.ringsurvey.business.pojo.po.payOrder.BaseBuyRecord;
import com.monetware.ringsurvey.business.pojo.vo.user.UserBuyRecordVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author Simo
 * @date 2019-02-27
 */
@Mapper
@Repository
public interface BuyRecordDao extends MyMapper<BaseBuyRecord> {

    List<BuyRecordDTO> getBuyRecord(UserBuyRecordVO param);

    List<BuyRecordDTO> getBuyRecordTest(@Param("userId") Integer userId);

}
