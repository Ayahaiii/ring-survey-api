package com.monetware.ringsurvey.business.pojo.dto.history;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectExportHistory;
import lombok.Data;

/**
 * @author Linked
 * @date 2020/6/10 14:45
 */
@Data
public class ExportHistoryDTO extends BaseProjectExportHistory {

    private String fileSizeStr;

}
