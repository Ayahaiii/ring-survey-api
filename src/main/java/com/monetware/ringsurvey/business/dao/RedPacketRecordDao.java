package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.redpacket.RedPacketRecordListDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseRedPacketRecord;
import com.monetware.ringsurvey.business.pojo.vo.redpacket.RedPacketRecordListVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RedPacketRecordDao extends MyMapper<BaseRedPacketRecord> {

    List<RedPacketRecordListDTO> getRedPacketRecordList(RedPacketRecordListVO redPacketListVO);
}
