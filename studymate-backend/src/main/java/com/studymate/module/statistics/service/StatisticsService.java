package com.studymate.module.statistics.service;

import com.studymate.module.statistics.vo.CategoryStatisticVO;
import com.studymate.module.statistics.vo.DashboardVO;
import com.studymate.module.statistics.vo.EmotionTrendVO;
import com.studymate.module.statistics.vo.StudyTrendVO;
import com.studymate.module.statistics.vo.WeakPointRankVO;

import java.util.List;

public interface StatisticsService {

    DashboardVO getDashboard(Long currentUserId);

    List<StudyTrendVO> getTrend(Long currentUserId);

    List<CategoryStatisticVO> getCategoryStatistics(Long currentUserId);

    List<WeakPointRankVO> getWeakPointRank(Long currentUserId);

    List<EmotionTrendVO> getEmotionTrend(Long currentUserId);
}
