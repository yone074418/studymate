package com.studymate.module.statistics.service;

import com.studymate.module.statistics.service.impl.StatisticsServiceImpl;
import com.studymate.module.statistics.vo.CategoryStatisticVO;
import com.studymate.module.statistics.vo.DashboardVO;
import com.studymate.module.statistics.vo.EmotionTrendVO;
import com.studymate.module.statistics.vo.StudyTrendVO;
import com.studymate.module.statistics.vo.WeakPointRankVO;
import com.studymate.module.study.entity.StudyRecord;
import com.studymate.module.study.mapper.StudyRecordCategoryMapper;
import com.studymate.module.study.mapper.StudyRecordMapper;
import com.studymate.module.study.mapper.WeakPointMapper;
import com.studymate.security.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatisticsServiceTest {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-06-09T10:00:00Z"), ZONE_ID);

    private StudyRecordMapper studyRecordMapper;
    private StudyRecordCategoryMapper studyRecordCategoryMapper;
    private WeakPointMapper weakPointMapper;
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        studyRecordMapper = mock(StudyRecordMapper.class);
        studyRecordCategoryMapper = mock(StudyRecordCategoryMapper.class);
        weakPointMapper = mock(WeakPointMapper.class);
        statisticsService = new StatisticsServiceImpl(studyRecordMapper, studyRecordCategoryMapper, weakPointMapper, FIXED_CLOCK);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new LoginUser(7L, "alice"),
                null,
                List.of()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void dashboardAggregatesDurationsWeakPointsEmotionTrendAndContinuousDaysForCurrentUser() {
        when(studyRecordMapper.selectList(any())).thenReturn(List.of(
                record(1L, 7L, LocalDate.of(2026, 6, 9), 60, "calm", 10),
                record(2L, 7L, LocalDate.of(2026, 6, 8), 90, "tired", 9),
                record(3L, 7L, LocalDate.of(2026, 6, 7), 30, "calm", 8),
                record(4L, 7L, LocalDate.of(2026, 5, 31), 120, "happy", 7)
        ));
        when(weakPointMapper.selectWeakPointRank(7L, 10)).thenReturn(List.of(
                new WeakPointRankVO("AOF and RDB", 3),
                new WeakPointRankVO("Index usage", 1)
        ));

        DashboardVO dashboard = statisticsService.getDashboard(7L);

        assertThat(dashboard.getTodayDurationMinutes()).isEqualTo(60);
        assertThat(dashboard.getWeekDurationMinutes()).isEqualTo(150);
        assertThat(dashboard.getMonthDurationMinutes()).isEqualTo(180);
        assertThat(dashboard.getTotalDurationMinutes()).isEqualTo(300);
        assertThat(dashboard.getContinuousStudyDays()).isEqualTo(3);
        assertThat(dashboard.getRecentEmotionStatus()).isEqualTo("calm");
        assertThat(dashboard.getRecentWeakPoints()).extracting(WeakPointRankVO::getContent)
                .containsExactly("AOF and RDB", "Index usage");
        assertThat(dashboard.getRecentTrend()).hasSize(7);
        assertThat(dashboard.getRecentTrend().get(6).getDurationMinutes()).isEqualTo(60);
        verify(weakPointMapper).selectWeakPointRank(7L, 10);
    }

    @Test
    void dashboardReturnsDefaultsWhenCurrentUserHasNoData() {
        when(studyRecordMapper.selectList(any())).thenReturn(List.of());
        when(weakPointMapper.selectWeakPointRank(7L, 10)).thenReturn(List.of());

        DashboardVO dashboard = statisticsService.getDashboard(7L);

        assertThat(dashboard.getTodayDurationMinutes()).isZero();
        assertThat(dashboard.getWeekDurationMinutes()).isZero();
        assertThat(dashboard.getMonthDurationMinutes()).isZero();
        assertThat(dashboard.getTotalDurationMinutes()).isZero();
        assertThat(dashboard.getContinuousStudyDays()).isZero();
        assertThat(dashboard.getRecentEmotionStatus()).isEqualTo("");
        assertThat(dashboard.getRecentWeakPoints()).isEmpty();
        assertThat(dashboard.getRecentTrend()).hasSize(7);
        assertThat(dashboard.getRecentTrend()).allMatch(item -> item.getDurationMinutes() == 0);
    }

    @Test
    void trendReturnsLastSevenDaysAndFillsMissingDaysWithZero() {
        when(studyRecordMapper.selectList(any())).thenReturn(List.of(
                record(1L, 7L, LocalDate.of(2026, 6, 3), 45, "calm", 1),
                record(2L, 7L, LocalDate.of(2026, 6, 9), 60, "focused", 2),
                record(3L, 7L, LocalDate.of(2026, 6, 9), 30, "tired", 3)
        ));

        List<StudyTrendVO> trend = statisticsService.getTrend(7L);

        assertThat(trend).extracting(StudyTrendVO::getDate).containsExactly(
                LocalDate.of(2026, 6, 3),
                LocalDate.of(2026, 6, 4),
                LocalDate.of(2026, 6, 5),
                LocalDate.of(2026, 6, 6),
                LocalDate.of(2026, 6, 7),
                LocalDate.of(2026, 6, 8),
                LocalDate.of(2026, 6, 9)
        );
        assertThat(trend).extracting(StudyTrendVO::getDurationMinutes)
                .containsExactly(45, 0, 0, 0, 0, 0, 90);
    }

    @Test
    void categoryStatisticSupportsRecordsWithMultipleCategories() {
        when(studyRecordCategoryMapper.selectCategoryStatistics(7L)).thenReturn(List.of(
                new CategoryStatisticVO("Redis", 2, 150, 50.0),
                new CategoryStatisticVO("MySQL", 1, 90, 30.0),
                new CategoryStatisticVO("Java", 1, 60, 20.0)
        ));

        List<CategoryStatisticVO> categories = statisticsService.getCategoryStatistics(7L);

        assertThat(categories).extracting(CategoryStatisticVO::getCategoryName)
                .containsExactly("Redis", "MySQL", "Java");
        assertThat(categories).extracting(CategoryStatisticVO::getRecordCount)
                .containsExactly(2, 1, 1);
        verify(studyRecordCategoryMapper).selectCategoryStatistics(7L);
    }

    @Test
    void weakPointRankReturnsCurrentUsersRankSortedByMapperResult() {
        when(weakPointMapper.selectWeakPointRank(7L, 10)).thenReturn(List.of(
                new WeakPointRankVO("AOF and RDB", 3),
                new WeakPointRankVO("Spring Security", 2)
        ));

        List<WeakPointRankVO> rank = statisticsService.getWeakPointRank(7L);

        assertThat(rank).extracting(WeakPointRankVO::getCount).containsExactly(3, 2);
        verify(weakPointMapper).selectWeakPointRank(7L, 10);
    }

    @Test
    void emotionTrendUsesLatestRecordOfEachDayAndDefaultForMissingDays() {
        when(studyRecordMapper.selectList(any())).thenReturn(List.of(
                record(1L, 7L, LocalDate.of(2026, 6, 9), 60, "tired", 1),
                record(2L, 7L, LocalDate.of(2026, 6, 9), 30, "calm", 2),
                record(3L, 7L, LocalDate.of(2026, 6, 7), 90, "", 3)
        ));

        List<EmotionTrendVO> emotions = statisticsService.getEmotionTrend(7L);

        assertThat(emotions).hasSize(7);
        assertThat(emotions.get(4).getDate()).isEqualTo(LocalDate.of(2026, 6, 7));
        assertThat(emotions.get(4).getEmotionStatus()).isEqualTo("平静");
        assertThat(emotions.get(6).getEmotionStatus()).isEqualTo("calm");
    }

    private StudyRecord record(Long id, Long userId, LocalDate recordDate, int durationMinutes, String emotionStatus, int createHour) {
        StudyRecord record = new StudyRecord();
        record.setId(id);
        record.setUserId(userId);
        record.setRecordDate(recordDate);
        record.setDurationMinutes(durationMinutes);
        record.setEmotionStatus(emotionStatus);
        record.setDeleted(0);
        record.setCreateTime(LocalDateTime.of(recordDate, java.time.LocalTime.of(createHour, 0)));
        return record;
    }
}
