package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.redpacket.RedPacketConfigDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseRedPacketConfig;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RedPacketConfigDao extends MyMapper<BaseRedPacketConfig> {

    RedPacketConfigDTO getRedPacketConfigDTO(Integer projectId);

}
