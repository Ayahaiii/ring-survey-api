package com.monetware.ringsurvey.business.pojo.dto.qnaire;

import com.monetware.ringsurvey.business.pojo.po.BaseProjectModule;
import lombok.Data;

import java.util.List;

/**
 * @author Simo
 * @date 2020-03-25
 */
@Data
public class ModuleImportDTO {

    private List<String> moduleCodes;

    private List<BaseProjectModule> modules;

    private BaseProjectModule module;
}
