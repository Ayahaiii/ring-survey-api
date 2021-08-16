package com.monetware.ringsurvey.business.dao;

import com.monetware.ringsurvey.business.pojo.vo.analyzer.AnalysisInsectSearchVO;
import com.monetware.ringsurvey.business.pojo.vo.analyzer.AnalysisMarkVO;
import com.monetware.ringsurvey.business.pojo.vo.analyzer.AnalysisSingleSearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface ProjectAnalyzerDao {

    List<HashMap<String, Object>> getInsectSampleProperty(AnalysisInsectSearchVO insectSearchVO);

    List<HashMap<String, Object>> getInsectSamplesProperty(AnalysisInsectSearchVO insectSearchVO);


    List<HashMap<String, Object>> getInsectQnaireStatisticsList(AnalysisInsectSearchVO insectSearchVO);

    List<HashMap<String, Object>> getInsectQnaireAndQnaireStatisticsList(AnalysisInsectSearchVO insectSearchVO);

    HashMap<String, Object> getSingleStatisticsList(AnalysisSingleSearchVO singleSearchVO);

    List<HashMap<String, Object>> getYbStatisticsScoreList(AnalysisMarkVO analysisMarkVO);

    List<HashMap<String, Object>> getMkStatisticsScoreList(AnalysisMarkVO analysisMarkVO);

    List<HashMap<String, Object>> getFyStatisticsScoreList(AnalysisMarkVO analysisMarkVO);


}
