package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.monitor.GetSampleCountByAddressDTO;
import com.monetware.ringsurvey.business.pojo.dto.monitor.SampleDashboardDTO;
import com.monetware.ringsurvey.business.pojo.dto.monitor.SampleStatusStatisticsDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SampleAssignNameDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SampleListDTO;
import com.monetware.ringsurvey.business.pojo.dto.sample.SampleStatusDistributionDTO;
import com.monetware.ringsurvey.business.pojo.po.BaseProjectSample;
import com.monetware.ringsurvey.business.pojo.vo.monitor.SampleStatusStatisticsVO;
import com.monetware.ringsurvey.business.pojo.vo.sample.*;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author Simo
 * @date 2019-06-17
 */
@Mapper
@Repository
public interface ProjectSampleDao extends MyMapper<BaseProjectSample> {

    Integer updateSampleList(@Param("projectId") Integer projectId, @Param("list") List<BaseProjectSample> list);

    Integer updateSampleListByCode(@Param("projectId") Integer projectId, @Param("list") List<BaseProjectSample> list);

    /**
     * 查询研究对象列表
     *
     * @param sampleListVO
     * @return
     */
    List<SampleListDTO> getSampleList(SampleListVO sampleListVO);

    List<SampleListDTO> getSampleListByOther(SampleListVO sampleListVO);

    List<SampleListDTO> getSampleListByIds(@Param("projectId") Integer projectId, @Param("list") List<Integer> list);

    /**
     * 删除研究对象包括批量删除
     *
     * @param deleteSampleVO
     * @return
     */
    Integer deleteSample(DeleteSampleVO deleteSampleVO);

    /**
     * 清空未接触样本
     *
     * @param clearInitialVO
     * @return
     */
    int clearInitial(ClearInitialVO clearInitialVO);

    /**
     * 去除初始化的重复样本
     *
     * @param repeatVO
     * @return
     */
    int deleteDuplicate(SampleRepeatVO repeatVO);

    /**
     * 更新研究对象
     *
     * @param projectSampleVO
     * @return
     */
    Integer updateProjectSample(ProjectSampleVO projectSampleVO);

    /**
     * 更新研究对象状态
     *
     * @param updateVO
     * @return
     */
    Integer updateSamplesStatus(SampleUpdateVO updateVO);

    Integer checkSampleExistByCode(@Param("projectId") Integer projectId, @Param("code") String code, @Param("sampleId") Integer sampleId);

    BaseProjectSample getSampleGuidByCode(@Param("projectId") Integer projectId, @Param("code") String code);

    /**
     * 获取map类型结果集的样本
     *
     * @param sampleId
     * @return
     */
    HashMap<String, Object> getSampleMapById(@Param("projectId") Integer projectId, @Param("sampleId") Integer sampleId);

    HashMap<String, Object> getSampleMapByGuid(@Param("projectId") Integer projectId, @Param("sampleGuid") String sampleGuid);

    List<SampleAssignNameDTO> getSampleAssignName(@Param("projectId") Integer projectId, @Param("sampleGuid") String sampleGuid);

    List<HashMap<String, Object>> getSampleMapListByStatus(@Param("projectId") Integer projectId, @Param("statusList") List<Integer> statusList);

    List<HashMap<String, Object>> getSampleMapListByStatusUserId(SampleOptionVO sampleOptionVO);

    List<HashMap<String, Object>> getSampleMapListByIds(@Param("projectId") Integer projectId,
                                                        @Param("statusList") List<Integer> statusList,
                                                        @Param("list") List<Integer> list);

    List<HashMap<String, Object>> getSampleMapListByIdsUserId(SampleOptionVO sampleOptionVO);
    //============================================ lu Begin =======================================

    /**
     * @Author: lu
     * @Date: 2020/4/1  下午7:34
     * @Description:获取样本状态分布
     **/
    List<SampleStatusDistributionDTO> getSampleStatusDistribution(Integer projectId);

    /**
     * 样本状态统计
     *
     * @param statisticsVO
     * @return
     */
    List<HashMap<String, Object>> getSampleStatusStatistics(SampleStatusStatisticsVO statisticsVO);

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午1:46
     * @Description:获取各省份样本数量汇总
     **/
    List<GetSampleCountByAddressDTO> getSampleByCountry(Integer projectId);

    /**
     * @Author: lu
     * @Date: 2020/4/2  上午11:09
     * @Description:显示当前省份各市样本数量汇总
     **/
    List<GetSampleCountByAddressDTO> getSampleCountByProvince(@Param("projectId") Integer projectId,
                                                              @Param("province") String province);

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午1:20
     * @Description:显示当前市各区样本数量汇总
     **/
    List<GetSampleCountByAddressDTO> getSampleCountByCity(@Param("projectId") Integer projectId,
                                                          @Param("province") String province,
                                                          @Param("city") String city);

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午1:26
     * @Description:显示当前区样本位置
     **/
    List<GetSampleCountByAddressDTO> getSampleLocation(@Param("projectId") Integer projectId, @Param("province") String province,
                                                       @Param("city") String city, @Param("district") String district);

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午2:16
     * @Description:获取仪表盘
     **/
    SampleDashboardDTO getSampleDashboard(Integer projectId);
    //============================================ lu End =========================================

    List<String> getAssignSampleGuids(AssignTeamVO assignTeamVO);
}
