package com.studymate.module.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.module.ai.dto.AiAnalyzeRequestDTO;
import com.studymate.module.ai.entity.AiCallLog;
import com.studymate.module.ai.mapper.AiCallLogMapper;
import com.studymate.module.ai.service.impl.AiRecordAnalyzeServiceImpl;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AiRecordAnalyzeServiceTest {

    private AiCallLogMapper aiCallLogMapper;
    private AiRecordAnalyzeService aiRecordAnalyzeService;

    @BeforeEach
    void setUp() {
        aiCallLogMapper = mock(AiCallLogMapper.class);
        aiRecordAnalyzeService = new AiRecordAnalyzeServiceImpl(aiCallLogMapper, new ObjectMapper());
    }

    @Test
    void analyzeReturnsCompleteMockResultForNormalNaturalLanguageInputAndSavesLog() {
        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(7L, buildRequest("今天学了 Redis 持久化两个小时，有点累。"));

        assertMockResult(result);
        ArgumentCaptor<AiCallLog> logCaptor = ArgumentCaptor.forClass(AiCallLog.class);
        verify(aiCallLogMapper).insert(logCaptor.capture());
        AiCallLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getUserId()).isEqualTo(7L);
        assertThat(savedLog.getRequestType()).isEqualTo("record_analyze");
        assertThat(savedLog.getRequestContent()).isEqualTo("今天学了 Redis 持久化两个小时，有点累。");
        assertThat(savedLog.getResponseContent()).contains("\"durationMinutes\":120");
        assertThat(savedLog.getResponseContent()).contains("\"categories\":[\"Redis\"]");
        assertThat(savedLog.getSuccess()).isEqualTo(1);
        assertThat(savedLog.getDurationMs()).isNotNegative();
        assertThat(savedLog.getCreateTime()).isNotNull();
        assertThat(savedLog.getResponseContent()).doesNotContain("password");
        assertThat(savedLog.getResponseContent()).doesNotContain("email");
    }

    @Test
    void analyzeAcceptsVeryShortInputAndStillReturnsMockResult() {
        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(7L, buildRequest("Redis"));

        assertMockResult(result);
    }

    @Test
    void analyzeStillReturnsMockResultWhenLogSaveFails() {
        doThrow(new RuntimeException("database unavailable")).when(aiCallLogMapper).insert(any(AiCallLog.class));

        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(7L, buildRequest("今天学了 Redis。"));

        assertMockResult(result);
    }

    private AiAnalyzeRequestDTO buildRequest(String rawContent) {
        AiAnalyzeRequestDTO requestDTO = new AiAnalyzeRequestDTO();
        requestDTO.setRawContent(rawContent);
        return requestDTO;
    }

    private void assertMockResult(AiAnalyzeResultVO result) {
        assertThat(result.getDurationMinutes()).isEqualTo(120);
        assertThat(result.getCategories()).containsExactly("Redis");
        assertThat(result.getStudyContent()).isEqualTo("今天主要学习了 Redis 持久化相关内容。");
        assertThat(result.getWeakPoints()).containsExactly("AOF 和 RDB 的区别还不够清楚");
        assertThat(result.getEmotionStatus()).isEqualTo("有点累");
        assertThat(result.getTomorrowPlan()).isEqualTo("明天可以先整理一张 AOF 和 RDB 的对比表。");
        assertThat(result.getAiSummary()).isEqualTo("今天学习了 Redis 持久化相关内容，并发现了需要继续巩固的薄弱点。");
        assertThat(result.getAiComfort()).isEqualTo("能发现自己哪里不懂，本身就是一种进步。今天已经做得不错了。");
    }
}
