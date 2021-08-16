package com.monetware.ringsurvey.business.pojo.dto.redpacket;

import com.github.pagehelper.Page;
import com.monetware.ringsurvey.system.base.PageList;
import lombok.Data;


@Data
public class RedPacketResultDTO {

    private Integer waitCount;

    private Integer successCount;

    private Integer failCount;

    private PageList<Page> pageList;

}
