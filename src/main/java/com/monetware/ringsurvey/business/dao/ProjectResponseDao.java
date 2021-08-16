package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.dto.monitor.*;
import com.monetware.ringsurvey.business.pojo.dto.response.*;
import com.monetware.ringsurvey.business.pojo.vo.monitor.GetBrowserParamVO;
import com.monetware.ringsurvey.business.pojo.vo.response.*;
import com.monetware.ringsurvey.business.pojo.po.BaseResponse;
import com.monetware.ringsurvey.business.pojo.vo.monitor.AnswerProcessVO;
import com.monetware.ringsurvey.system.base.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ProjectResponseDao extends MyMapper<BaseResponse> {

    List<Map<String, Object>> getAnswerProcessData(AnswerProcessVO processVO);

    List<Map<String, Object>> getAnswerTimeData(@Param("projectId") Integer projectId, @Param("type") Integer type,
                                                @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                @Param("qnaireId") Integer qnaireId);

    List<AnswerLonALatDTO> getSampleLonAndLatData(@Param("projectId") Integer projectId);


    /**
     * 获取答卷列表
     *
     * @param listVO
     * @return
     */
    List<ResponseListDTO> getResponseList(ResponseListVO listVO);

    /**
     * 答卷列表页面搜索条件
     *
     * @param projectId
     * @return
     */
    List<SearchInfoDTO> getSearchInfo(@Param("projectId") Integer projectId);

    /**
     * 审核抽样
     *
     * @param samplingCount
     * @return
     */
    Integer updateResponseSampling(@Param("projectId") Integer projectId, @Param("samplingCount") Integer samplingCount);

    /**
     * 重置抽审
     *
     * @param projectId
     * @return
     */
    Integer updateResponseInit(@Param("projectId") Integer projectId);

    /**
     * 审核页面相关信息
     *
     * @param info
     * @return
     */
    ResponseAuditInfoDTO getResponseAuditInfo(ResponseAuditInfoVO info);

    /**
     * 答卷审核日志
     *
     * @param responseGuid
     * @param projectId
     * @return
     */
    List<ResponseAuditLogDTO> getResponseAuditLogs(@Param("responseGuid") String responseGuid, @Param("projectId") Integer projectId);

    /**
     * 答卷导出检索条件查询
     *
     * @param searchVO
     * @return
     */
    List<ResponseExportSelectDTO> getExportResponseList(ResponseExportSearchVO searchVO);

    /**
     * 答卷导出
     *
     * @param projectId
     * @param questionnaireId
     * @param list
     * @return
     */
    List<ResponseExportSelectDTO> getExportResponseListByIds(@Param("projectId") Integer projectId,
                                                             @Param("questionnaireId") Integer questionnaireId,
                                                             @Param("list") List<Integer> list);

    /**
     * 批量删除答卷
     *
     * @param projectId
     * @param list
     * @param userId
     * @return
     */
    int deleteBySampleGuids(@Param("projectId") Integer projectId, @Param("list") List<String> list, @Param("userId") Integer userId);

    //============================================ lu Begin =======================================

    /**
     * @Author: lu
     * @Date: 2020/4/2  上午10:27
     * @Description:获取地图坐标信息(前100)
     **/
    List<ResponseLocationDTO> getResponseLocation(Integer projectId);

    /**
     * @Author: lu
     * @Date: 2020/4/2  下午7:50
     * @Description:获取来源报告
     **/
    List<GetSourceReportDTO> getSourceReport(Integer projectId);

    /**
     * @Author: lu
     * @Date: 2020/4/14  下午7:21
     * @Description:获取答卷list
     **/
    List<GetQuestionnaireListDTO> getQuestionnaireList(Integer projectId);

    /**
     * @Author: lu
     * @Date: 2020/4/20  下午6:16
     * @Description:获取浏览器参数
     **/
    List<GetBrowserParamDTO> getBrowserParam(GetBrowserParamVO param);

    /**
     * @Author: lu
     * @Date: 2020/4/21  上午10:31
     * @Description:Chrome的版本分布
     **/
    List<GetBrowserParamDTO> getChromeVersionParam(GetBrowserParamVO param);

    /**
     * @Author: lu
     * @Date: 2020/4/21  上午10:31
     * @Description:QQ浏览器的版本分布
     **/
    List<GetBrowserParamDTO> getQqVersionParam(GetBrowserParamVO param);

    /**
     * @Author: lu
     * @Date: 2020/4/21  上午10:31
     * @Description:Safari的版本分布
     **/
    List<GetBrowserParamDTO> getSafariVersionParam(GetBrowserParamVO param);

    /**
     * @Author: lu
     * @Date: 2020/4/21  下午12:59
     * @Description:浏览器类型加浏览器版本统计数量
     **/
    List<GetBrowserParamDTO> getCountWithTypeAndVersion(GetBrowserParamVO param);


    //============================================ lu End =========================================
}
