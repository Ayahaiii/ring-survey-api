package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.quota.ModuleSampleDTO;
import com.monetware.ringsurvey.business.pojo.dto.quota.ProjectQuotaDTO;
import com.monetware.ringsurvey.business.pojo.dto.quota.QuotaExportDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectQuota;
import com.monetware.ringsurvey.business.pojo.vo.quota.ProjectQuotaListVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProjectQuotaDao extends MyMapper<BaseProjectQuota> {

    /**
     * 配额列表
     *
     * @param quotaListVO
     * @return
     */
    List<ProjectQuotaDTO> getProjectQuotaList(ProjectQuotaListVO quotaListVO);

    List<BaseProjectQuota> getQuotaList(@Param("projectId") Integer projectId, @Param("status") Integer status);

    /**
     * 查询是否存在重复配额名称
     *
     * @param projectId
     * @param name
     * @return
     */
    Integer checkQuotaExistByName(@Param("projectId") Integer projectId, @Param("name") String name, @Param("oldName") String oldName);

    /**
     * 获取问卷模块和样本属性
     *
     * @param projectId
     * @return
     */
    List<ModuleSampleDTO> getModuleSample(Integer projectId);

    /**
     * 配额导出数据
     *
     * @param quotaIds
     * @return
     */
    List<QuotaExportDTO> exportQuota(@Param("quotaIds") List<Integer> quotaIds);

    /**
     * 批量导入配额
     *
     * @param quotaList
     * @return
     */
    Integer insertBatchByImport(@Param("quotaList") List<BaseProjectQuota> quotaList);

}
