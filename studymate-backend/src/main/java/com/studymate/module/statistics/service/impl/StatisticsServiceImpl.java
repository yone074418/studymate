package com.studymate.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studymate.module.statistics.service.StatisticsService;
import com.studymate.module.statistics.vo.CategoryStatisticVO;
import com.studymate.module.statistics.vo.DashboardVO;
import com.studymate.module.statistics.vo.EmotionTrendVO;
import com.studymate.module.statistics.vo.StudyTrendVO;
import com.studymate.module.statistics.vo.WeakPointRankVO;
import com.studymate.module.study.entity.StudyRecord;
import com.studymate.module.study.mapper.StudyRecordCategoryMapper;
import com.studymate.module.study.mapper.StudyRecordMapper;
import com.studymate.module.study.mapper.WeakPointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final int NOT_DELETED = 0;
    private static final int RECENT_DAYS = 7;
    private static final int WEAK_POINT_LIMIT = 10;
    private static final String DEFAULT_EMOTION_STATUS = "平静";

    private final StudyRecordMapper studyRecordMapper;
    private final StudyRecordCategoryMapper studyRecordCategoryMapper;
    private final WeakPointMapper weakPointMapper;
    private final Clock clock;

    @Autowired
    public StatisticsServiceImpl(
            StudyRecordMapper studyRecordMapper,
            StudyRecordCategoryMapper studyRecordCategoryMapper,
            WeakPointMapper weakPointMapper
    ) {
        this(studyRecordMapper, studyRecordCategoryMapper, weakPointMapper, Clock.systemDefaultZone());
    }

    public StatisticsServiceImpl(
            StudyRecordMapper studyRecordMapper,
            StudyRecordCategoryMapper studyRecordCategoryMapper,
            WeakPointMapper weakPointMapper,
            Clock clock
    ) {
        this.studyRecordMapper = studyRecordMapper;
        this.studyRecordCategoryMapper = studyRecordCategoryMapper;
        this.weakPointMapper = weakPointMapper;
        this.clock = clock;
    }

    @Override
    public DashboardVO getDashboard(Long currentUserId) {
        LocalDate today = LocalDate.now(clock);
        List<StudyRecord> records = loadActiveRecords(currentUserId);

        DashboardVO dashboard = new DashboardVO();
        dashboard.setTodayDurationMinutes(sumDuration(records, today, today));
        dashboard.setWeekDurationMinutes(sumDuration(records, startOfWeek(today), today));
        dashboard.setMonthDurationMinutes(sumDuration(records, today.withDayOfMonth(1), today));
        dashboard.setTotalDurationMinutes(records.stream().mapToInt(this::durationOf).sum());
        dashboard.setContinuousStudyDays(calculateContinuousStudyDays(records, today));
        dashboard.setRecentWeakPoints(weakPointMapper.selectWeakPointRank(currentUserId, WEAK_POINT_LIMIT));
        dashboard.setRecentEmotionStatus(resolveRecentEmotion(records));
        dashboard.setRecentTrend(buildStudyTrend(records, today));
        return dashboard;
    }

    @Override
    public List<StudyTrendVO> getTrend(Long currentUserId) {
        return buildStudyTrend(loadActiveRecords(currentUserId), LocalDate.now(clock));
    }

    @Override
    public List<CategoryStatisticVO> getCategoryStatistics(Long currentUserId) {
        return studyRecordCategoryMapper.selectCategoryStatistics(currentUserId);
    }

    @Override
    public List<WeakPointRankVO> getWeakPointRank(Long currentUserId) {
        return weakPointMapper.selectWeakPointRank(currentUserId, WEAK_POINT_LIMIT);
    }

    @Override
    public List<EmotionTrendVO> getEmotionTrend(Long currentUserId) {
        LocalDate today = LocalDate.now(clock);
        List<StudyRecord> records = loadActiveRecords(currentUserId);
        Map<LocalDate, StudyRecord> latestRecordByDate = records.stream()
                .filter(record -> record.getRecordDate() != null)
                .collect(Collectors.toMap(
                        StudyRecord::getRecordDate,
                        record -> record,
                        (left, right) -> compareByCreateTime(left, right) >= 0 ? left : right,
                        LinkedHashMap::new
                ));

        return recentDates(today).stream()
                .map(date -> new EmotionTrendVO(date, resolveEmotion(latestRecordByDate.get(date))))
                .toList();
    }

    private List<StudyRecord> loadActiveRecords(Long currentUserId) {
        return studyRecordMapper.selectList(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getUserId, currentUserId)
                .eq(StudyRecord::getDeleted, NOT_DELETED)
                .orderByAsc(StudyRecord::getRecordDate)
                .orderByAsc(StudyRecord::getCreateTime));
    }

    private List<StudyTrendVO> buildStudyTrend(List<StudyRecord> records, LocalDate today) {
        Map<LocalDate, Integer> durationByDate = records.stream()
                .filter(record -> record.getRecordDate() != null)
                .filter(record -> !record.getRecordDate().isBefore(today.minusDays(RECENT_DAYS - 1L)))
                .filter(record -> !record.getRecordDate().isAfter(today))
                .collect(Collectors.groupingBy(
                        StudyRecord::getRecordDate,
                        Collectors.summingInt(this::durationOf)
                ));

        return recentDates(today).stream()
                .map(date -> new StudyTrendVO(date, durationByDate.getOrDefault(date, 0)))
                .toList();
    }

    private List<LocalDate> recentDates(LocalDate today) {
        LocalDate startDate = today.minusDays(RECENT_DAYS - 1L);
        return java.util.stream.IntStream.range(0, RECENT_DAYS)
                .mapToObj(startDate::plusDays)
                .toList();
    }

    private Integer sumDuration(List<StudyRecord> records, LocalDate startDate, LocalDate endDate) {
        return records.stream()
                .filter(record -> record.getRecordDate() != null)
                .filter(record -> !record.getRecordDate().isBefore(startDate))
                .filter(record -> !record.getRecordDate().isAfter(endDate))
                .mapToInt(this::durationOf)
                .sum();
    }

    private int calculateContinuousStudyDays(List<StudyRecord> records, LocalDate today) {
        Set<LocalDate> studyDates = records.stream()
                .map(StudyRecord::getRecordDate)
                .filter(date -> date != null && !date.isAfter(today))
                .collect(Collectors.toSet());
        if (studyDates.isEmpty()) {
            return 0;
        }

        LocalDate cursor = studyDates.contains(today)
                ? today
                : studyDates.stream().max(Comparator.naturalOrder()).orElse(today);
        int continuousDays = 0;
        while (studyDates.contains(cursor)) {
            continuousDays++;
            cursor = cursor.minusDays(1);
        }
        return continuousDays;
    }

    private String resolveRecentEmotion(List<StudyRecord> records) {
        return records.stream()
                .max(this::compareByRecordDateAndCreateTime)
                .map(this::resolveEmotion)
                .orElse("");
    }

    private String resolveEmotion(StudyRecord record) {
        if (record == null || record.getEmotionStatus() == null || record.getEmotionStatus().isBlank()) {
            return DEFAULT_EMOTION_STATUS;
        }
        return record.getEmotionStatus().trim();
    }

    private int compareByRecordDateAndCreateTime(StudyRecord left, StudyRecord right) {
        int dateCompare = nullSafeDate(left).compareTo(nullSafeDate(right));
        if (dateCompare != 0) {
            return dateCompare;
        }
        return compareByCreateTime(left, right);
    }

    private int compareByCreateTime(StudyRecord left, StudyRecord right) {
        if (left.getCreateTime() == null && right.getCreateTime() == null) {
            return 0;
        }
        if (left.getCreateTime() == null) {
            return -1;
        }
        if (right.getCreateTime() == null) {
            return 1;
        }
        return left.getCreateTime().compareTo(right.getCreateTime());
    }

    private LocalDate nullSafeDate(StudyRecord record) {
        return record.getRecordDate() == null ? LocalDate.MIN : record.getRecordDate();
    }

    private int durationOf(StudyRecord record) {
        return record.getDurationMinutes() == null ? 0 : record.getDurationMinutes();
    }

    private LocalDate startOfWeek(LocalDate today) {
        return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
