package com.studymate.module.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.module.ai.client.AiClient;
import com.studymate.module.ai.client.AiClientException;
import com.studymate.module.ai.config.AiProperties;
import com.studymate.module.ai.dto.AiAnalyzeRequestDTO;
import com.studymate.module.ai.entity.AiCallLog;
import com.studymate.module.ai.mapper.AiCallLogMapper;
import com.studymate.module.ai.parser.AiAnalyzeResultParser;
import com.studymate.module.ai.prompt.StudyRecordPromptBuilder;
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
import static org.mockito.Mockito.when;

class AiRecordAnalyzeServiceTest {

    private AiCallLogMapper aiCallLogMapper;
    private AiClient aiClient;
    private AiProperties aiProperties;
    private AiRecordAnalyzeService aiRecordAnalyzeService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        aiCallLogMapper = mock(AiCallLogMapper.class);
        aiClient = mock(AiClient.class);
        aiProperties = new AiProperties();
        aiProperties.setMockEnabled(true);
        aiProperties.setModel("test-model");
        aiRecordAnalyzeService = new AiRecordAnalyzeServiceImpl(
                aiCallLogMapper,
                objectMapper,
                aiProperties,
                new StudyRecordPromptBuilder(),
                aiClient,
                new AiAnalyzeResultParser(objectMapper)
        );
    }

    @Test
    void analyzeReturnsCompleteMockResultAndSavesSuccessLog() {
        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(7L, buildRequest("今天学了 Redis 持久化两个小时，有点累。"));

        assertMockResult(result);
        AiCallLog savedLog = captureLog();
        assertThat(savedLog.getUserId()).isEqualTo(7L);
        assertThat(savedLog.getRequestType()).isEqualTo("record_analyze");
        assertThat(savedLog.getModelName()).isEqualTo("mock");
        assertThat(savedLog.getPrompt()).contains("Only return valid JSON");
        assertThat(savedLog.getRequestContent()).isEqualTo("今天学了 Redis 持久化两个小时，有点累。");
        assertThat(savedLog.getResponseContent()).contains("\"durationMinutes\":120");
        assertThat(savedLog.getSuccess()).isEqualTo(1);
        assertThat(savedLog.getErrorMessage()).isNull();
        assertThat(savedLog.getDurationMs()).isNotNegative();
        assertThat(savedLog.getCreateTime()).isNotNull();
    }

    @Test
    void analyzeRealAiJsonReturnSavesSuccessLog() {
        aiProperties.setMockEnabled(false);
        when(aiClient.chat(any())).thenReturn("""
                {"durationMinutes":60,"categories":["Spring Boot"],"studyContent":"复习 Spring Security","weakPoints":[],"emotionStatus":"平静","tomorrowPlan":"看一个认证流程小例子","aiSummary":"完成了安全模块复盘","aiComfort":"节奏不错，继续保持轻量推进。"}
                """);

        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(3L, buildRequest("复习 Spring Security 1 小时"));

        assertThat(result.getDurationMinutes()).isEqualTo(60);
        assertThat(result.getCategories()).containsExactly("Spring Boot");
        AiCallLog savedLog = captureLog();
        assertThat(savedLog.getSuccess()).isEqualTo(1);
        assertThat(savedLog.getModelName()).isEqualTo("test-model");
        assertThat(savedLog.getResponseContent()).contains("Spring Security");
    }

    @Test
    void analyzeRealAiNonJsonReturnFallbackAndSavesFailureLog() {
        aiProperties.setMockEnabled(false);
        when(aiClient.chat(any())).thenReturn("I cannot return JSON today");

        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(3L, buildRequest("今天学习了 Java 集合"));

        assertThat(result.getStudyContent()).isEqualTo("今天学习了 Java 集合");
        assertThat(result.getAiComfort()).isEqualTo("今天能留下记录已经很好了，慢慢来，每一步都算数。");
        AiCallLog savedLog = captureLog();
        assertThat(savedLog.getSuccess()).isZero();
        assertThat(savedLog.getErrorMessage()).isEqualTo("AI returned non JSON");
        assertThat(savedLog.getResponseContent()).isEqualTo("I cannot return JSON today");
    }

    @Test
    void analyzeRealAiClientFailureReturnsFallbackAndSavesFailureLog() {
        aiProperties.setMockEnabled(false);
        when(aiClient.chat(any())).thenThrow(new AiClientException("AI service rate limited", "rate limited"));

        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(3L, buildRequest("今天学习 Redis"));

        assertThat(result.getStudyContent()).isEqualTo("今天学习 Redis");
        AiCallLog savedLog = captureLog();
        assertThat(savedLog.getSuccess()).isZero();
        assertThat(savedLog.getErrorMessage()).contains("rate limited");
        assertThat(savedLog.getResponseContent()).isEqualTo("rate limited");
    }

    @Test
    void analyzeStillReturnsResultWhenLogSaveFails() {
        doThrow(new RuntimeException("database unavailable")).when(aiCallLogMapper).insert(any(AiCallLog.class));

        AiAnalyzeResultVO result = aiRecordAnalyzeService.analyze(7L, buildRequest("今天学了 Redis。"));

        assertMockResult(result);
    }

    private AiAnalyzeRequestDTO buildRequest(String rawContent) {
        AiAnalyzeRequestDTO requestDTO = new AiAnalyzeRequestDTO();
        requestDTO.setRawContent(rawContent);
        return requestDTO;
    }

    private AiCallLog captureLog() {
        ArgumentCaptor<AiCallLog> logCaptor = ArgumentCaptor.forClass(AiCallLog.class);
        verify(aiCallLogMapper).insert(logCaptor.capture());
        return logCaptor.getValue();
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
