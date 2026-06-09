package com.studymate.module.statistics.controller;

import com.studymate.common.exception.GlobalExceptionHandler;
import com.studymate.module.statistics.service.StatisticsService;
import com.studymate.module.statistics.vo.CategoryStatisticVO;
import com.studymate.module.statistics.vo.DashboardVO;
import com.studymate.module.statistics.vo.EmotionTrendVO;
import com.studymate.module.statistics.vo.StudyTrendVO;
import com.studymate.module.statistics.vo.WeakPointRankVO;
import com.studymate.security.JwtAuthenticationFilter;
import com.studymate.security.JwtProperties;
import com.studymate.security.JwtUtil;
import com.studymate.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class, JwtProperties.class})
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void dashboardRequiresLogin() throws Exception {
        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void dashboardAllowsValidTokenAndReturnsAggregates() throws Exception {
        DashboardVO dashboard = new DashboardVO();
        dashboard.setTodayDurationMinutes(60);
        dashboard.setWeekDurationMinutes(150);
        dashboard.setMonthDurationMinutes(180);
        dashboard.setTotalDurationMinutes(300);
        dashboard.setContinuousStudyDays(3);
        dashboard.setRecentEmotionStatus("calm");
        dashboard.setRecentWeakPoints(List.of(new WeakPointRankVO("AOF and RDB", 3)));
        dashboard.setRecentTrend(List.of(new StudyTrendVO(LocalDate.of(2026, 6, 9), 60)));
        when(statisticsService.getDashboard(7L)).thenReturn(dashboard);

        mockMvc.perform(get("/api/statistics/dashboard")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(7L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.todayDurationMinutes").value(60))
                .andExpect(jsonPath("$.data.weekDurationMinutes").value(150))
                .andExpect(jsonPath("$.data.monthDurationMinutes").value(180))
                .andExpect(jsonPath("$.data.totalDurationMinutes").value(300))
                .andExpect(jsonPath("$.data.continuousStudyDays").value(3))
                .andExpect(jsonPath("$.data.recentWeakPoints[0].content").value("AOF and RDB"))
                .andExpect(jsonPath("$.data.recentEmotionStatus").value("calm"))
                .andExpect(jsonPath("$.data.recentTrend[0].durationMinutes").value(60));
    }

    @Test
    void trendAllowsValidTokenAndReturnsLastSevenDays() throws Exception {
        when(statisticsService.getTrend(7L)).thenReturn(List.of(new StudyTrendVO(LocalDate.of(2026, 6, 9), 90)));

        mockMvc.perform(get("/api/statistics/trend")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(7L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].date").value("2026-06-09"))
                .andExpect(jsonPath("$.data[0].durationMinutes").value(90));
    }

    @Test
    void categoryAllowsValidTokenAndReturnsCategoryStatistics() throws Exception {
        when(statisticsService.getCategoryStatistics(7L)).thenReturn(List.of(new CategoryStatisticVO("Redis", 2, 150, 60.0)));

        mockMvc.perform(get("/api/statistics/category")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(7L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].categoryName").value("Redis"))
                .andExpect(jsonPath("$.data[0].recordCount").value(2))
                .andExpect(jsonPath("$.data[0].durationMinutes").value(150))
                .andExpect(jsonPath("$.data[0].percentage").value(60.0));
    }

    @Test
    void weakPointsAllowsValidTokenAndReturnsRank() throws Exception {
        when(statisticsService.getWeakPointRank(7L)).thenReturn(List.of(new WeakPointRankVO("Spring Security", 2)));

        mockMvc.perform(get("/api/statistics/weak-points")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(7L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].content").value("Spring Security"))
                .andExpect(jsonPath("$.data[0].count").value(2));
    }

    @Test
    void emotionAllowsValidTokenAndReturnsEmotionTrend() throws Exception {
        when(statisticsService.getEmotionTrend(7L)).thenReturn(List.of(new EmotionTrendVO(LocalDate.of(2026, 6, 9), "平静")));

        mockMvc.perform(get("/api/statistics/emotion")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(7L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].date").value("2026-06-09"))
                .andExpect(jsonPath("$.data[0].emotionStatus").value("平静"));
    }
}
