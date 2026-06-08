package com.studymate.module.study.service;

import com.studymate.common.exception.BusinessException;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.entity.StudyRecord;
import com.studymate.module.study.entity.StudyRecordCategory;
import com.studymate.module.study.entity.WeakPoint;
import com.studymate.module.study.mapper.StudyRecordCategoryMapper;
import com.studymate.module.study.mapper.StudyRecordMapper;
import com.studymate.module.study.mapper.WeakPointMapper;
import com.studymate.module.study.service.impl.StudyRecordServiceImpl;
import com.studymate.module.study.vo.StudyRecordDetailVO;
import com.studymate.security.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudyRecordServiceTest {

    private StudyRecordMapper studyRecordMapper;
    private StudyRecordCategoryMapper studyRecordCategoryMapper;
    private WeakPointMapper weakPointMapper;
    private StudyRecordService studyRecordService;
    private AtomicLong idSequence;

    @BeforeEach
    void setUp() {
        studyRecordMapper = mock(StudyRecordMapper.class);
        studyRecordCategoryMapper = mock(StudyRecordCategoryMapper.class);
        weakPointMapper = mock(WeakPointMapper.class);
        studyRecordService = new StudyRecordServiceImpl(studyRecordMapper, studyRecordCategoryMapper, weakPointMapper);
        idSequence = new AtomicLong(100);
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
    void createSavesStudyRecordWithCurrentUserIdAndRequestFields() {
        when(studyRecordMapper.selectOne(any())).thenReturn(null);
        when(studyRecordMapper.insert(any(StudyRecord.class))).thenAnswer(invocation -> {
            StudyRecord record = invocation.getArgument(0);
            record.setId(idSequence.incrementAndGet());
            return 1;
        });

        StudyRecordDetailVO result = studyRecordService.createStudyRecord(buildRequest("first content"));

        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getRawContent()).isEqualTo("first content");
        ArgumentCaptor<StudyRecord> recordCaptor = ArgumentCaptor.forClass(StudyRecord.class);
        verify(studyRecordMapper).insert(recordCaptor.capture());
        StudyRecord savedRecord = recordCaptor.getValue();
        assertThat(savedRecord.getUserId()).isEqualTo(7L);
        assertThat(savedRecord.getRecordDate()).isEqualTo(LocalDate.of(2026, 6, 8));
        assertThat(savedRecord.getDurationMinutes()).isEqualTo(90);
        assertThat(savedRecord.getStudyContent()).isEqualTo("Redis persistence");
        assertThat(savedRecord.getEmotionStatus()).isEqualTo("tired");
        assertThat(savedRecord.getTomorrowPlan()).isEqualTo("Make a comparison table.");
        assertThat(savedRecord.getAiSummary()).isEqualTo("Reviewed Redis persistence.");
        assertThat(savedRecord.getAiComfort()).isEqualTo("Noticing the gap is progress.");
        assertThat(savedRecord.getRemark()).isEqualTo("Need review.");
        assertThat(savedRecord.getDeleted()).isZero();
    }

    @Test
    void createAllowsSameUserSameDateWithDifferentRawContent() {
        when(studyRecordMapper.selectOne(any())).thenReturn(null);
        when(studyRecordMapper.insert(any(StudyRecord.class))).thenAnswer(invocation -> {
            StudyRecord record = invocation.getArgument(0);
            record.setId(idSequence.incrementAndGet());
            return 1;
        });

        studyRecordService.createStudyRecord(buildRequest("first content"));
        studyRecordService.createStudyRecord(buildRequest("second content"));

        verify(studyRecordMapper, times(2)).insert(any(StudyRecord.class));
    }

    @Test
    void createRejectsDuplicateRawContentForSameUserAndSameDate() {
        StudyRecord existing = new StudyRecord();
        existing.setId(15L);
        existing.setUserId(7L);
        existing.setRecordDate(LocalDate.of(2026, 6, 8));
        existing.setRawContent("first content");
        when(studyRecordMapper.selectOne(any())).thenReturn(existing);

        assertThatThrownBy(() -> studyRecordService.createStudyRecord(buildRequest("first content")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Study record already exists");
        verify(studyRecordMapper, never()).insert(any(StudyRecord.class));
    }

    @Test
    void createSavesExistingCategoriesToRelationTable() {
        when(studyRecordMapper.selectOne(any())).thenReturn(null);
        when(studyRecordMapper.insert(any(StudyRecord.class))).thenAnswer(invocation -> {
            StudyRecord record = invocation.getArgument(0);
            record.setId(21L);
            return 1;
        });
        when(studyRecordCategoryMapper.selectCategoryIdByName(eq("Redis"))).thenReturn(4L);
        when(studyRecordCategoryMapper.selectCategoryIdByName(eq("Unknown"))).thenReturn(null);

        StudyRecordDetailVO result = studyRecordService.createStudyRecord(buildRequestWithCategories(List.of("Redis", "Unknown")));

        assertThat(result.getCategories()).containsExactly("Redis");
        ArgumentCaptor<StudyRecordCategory> categoryCaptor = ArgumentCaptor.forClass(StudyRecordCategory.class);
        verify(studyRecordCategoryMapper).insert(categoryCaptor.capture());
        StudyRecordCategory relation = categoryCaptor.getValue();
        assertThat(relation.getStudyRecordId()).isEqualTo(21L);
        assertThat(relation.getCategoryId()).isEqualTo(4L);
        assertThat(relation.getUserId()).isEqualTo(7L);
    }

    @Test
    void studyRecordCategoryEntityMatchesCurrentRelationTableColumns() {
        List<String> fieldNames = Arrays.stream(StudyRecordCategory.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .toList();

        assertThat(fieldNames).doesNotContain("deleted", "updateTime");
    }

    @Test
    void createSavesWeakPointsForCurrentUserAndStudyRecord() {
        when(studyRecordMapper.selectOne(any())).thenReturn(null);
        when(studyRecordMapper.insert(any(StudyRecord.class))).thenAnswer(invocation -> {
            StudyRecord record = invocation.getArgument(0);
            record.setId(31L);
            return 1;
        });

        StudyRecordDetailVO result = studyRecordService.createStudyRecord(buildRequest("first content"));

        assertThat(result.getWeakPoints()).containsExactly("AOF and RDB comparison");
        ArgumentCaptor<WeakPoint> weakPointCaptor = ArgumentCaptor.forClass(WeakPoint.class);
        verify(weakPointMapper).insert(weakPointCaptor.capture());
        WeakPoint weakPoint = weakPointCaptor.getValue();
        assertThat(weakPoint.getStudyRecordId()).isEqualTo(31L);
        assertThat(weakPoint.getUserId()).isEqualTo(7L);
        assertThat(weakPoint.getContent()).isEqualTo("AOF and RDB comparison");
        assertThat(weakPoint.getResolved()).isZero();
        assertThat(weakPoint.getDeleted()).isZero();
    }

    @Test
    void createAlwaysUsesCurrentUserIdInsteadOfRequestUserId() {
        when(studyRecordMapper.selectOne(any())).thenReturn(null);
        when(studyRecordMapper.insert(any(StudyRecord.class))).thenAnswer(invocation -> {
            StudyRecord record = invocation.getArgument(0);
            record.setId(41L);
            return 1;
        });

        studyRecordService.createStudyRecord(buildRequest("first content"));

        ArgumentCaptor<StudyRecord> recordCaptor = ArgumentCaptor.forClass(StudyRecord.class);
        verify(studyRecordMapper).insert(recordCaptor.capture());
        assertThat(recordCaptor.getValue().getUserId()).isEqualTo(7L);
    }

    private StudyRecordCreateDTO buildRequest(String rawContent) {
        StudyRecordCreateDTO request = buildRequestWithCategories(List.of("Redis"));
        request.setRawContent(rawContent);
        return request;
    }

    private StudyRecordCreateDTO buildRequestWithCategories(List<String> categories) {
        StudyRecordCreateDTO request = new StudyRecordCreateDTO();
        request.setRecordDate(LocalDate.of(2026, 6, 8));
        request.setRawContent("first content");
        request.setDurationMinutes(90);
        request.setStudyContent("Redis persistence");
        request.setCategories(categories);
        request.setWeakPoints(List.of("AOF and RDB comparison"));
        request.setEmotionStatus("tired");
        request.setTomorrowPlan("Make a comparison table.");
        request.setAiSummary("Reviewed Redis persistence.");
        request.setAiComfort("Noticing the gap is progress.");
        request.setRemark("Need review.");
        return request;
    }
}
