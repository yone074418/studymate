package com.studymate.module.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.module.ai.dto.AiAnalyzeRequestDTO;
import com.studymate.module.ai.entity.AiCallLog;
import com.studymate.module.ai.mapper.AiCallLogMapper;
import com.studymate.module.ai.service.AiRecordAnalyzeService;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AiRecordAnalyzeServiceImpl implements AiRecordAnalyzeService {

    private static final String REQUEST_TYPE_RECORD_ANALYZE = "record_analyze";
    private static final String MODEL_NAME_MOCK = "mock";
    private static final int SUCCESS = 1;

    private final AiCallLogMapper aiCallLogMapper;
    private final ObjectMapper objectMapper;

    public AiRecordAnalyzeServiceImpl(AiCallLogMapper aiCallLogMapper, ObjectMapper objectMapper) {
        this.aiCallLogMapper = aiCallLogMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiAnalyzeResultVO analyze(Long currentUserId, AiAnalyzeRequestDTO requestDTO) {
        long startTime = System.currentTimeMillis();
        AiAnalyzeResultVO resultVO = buildMockResult();
        saveSuccessLog(currentUserId, requestDTO.getRawContent(), resultVO, elapsedMs(startTime));
        return resultVO;
    }

    private AiAnalyzeResultVO buildMockResult() {
        AiAnalyzeResultVO resultVO = new AiAnalyzeResultVO();
        resultVO.setDurationMinutes(120);
        resultVO.setCategories(List.of("Redis"));
        resultVO.setStudyContent("今天主要学习了 Redis 持久化相关内容。");
        resultVO.setWeakPoints(List.of("AOF 和 RDB 的区别还不够清楚"));
        resultVO.setEmotionStatus("有点累");
        resultVO.setTomorrowPlan("明天可以先整理一张 AOF 和 RDB 的对比表。");
        resultVO.setAiSummary("今天学习了 Redis 持久化相关内容，并发现了需要继续巩固的薄弱点。");
        resultVO.setAiComfort("能发现自己哪里不懂，本身就是一种进步。今天已经做得不错了。");
        return resultVO;
    }

    private void saveSuccessLog(Long currentUserId, String rawContent, AiAnalyzeResultVO resultVO, int durationMs) {
        try {
            AiCallLog aiCallLog = new AiCallLog();
            aiCallLog.setUserId(currentUserId);
            aiCallLog.setRequestType(REQUEST_TYPE_RECORD_ANALYZE);
            aiCallLog.setModelName(MODEL_NAME_MOCK);
            aiCallLog.setRequestContent(rawContent);
            aiCallLog.setResponseContent(toJson(resultVO));
            aiCallLog.setSuccess(SUCCESS);
            aiCallLog.setDurationMs(durationMs);
            aiCallLog.setCreateTime(LocalDateTime.now());
            aiCallLogMapper.insert(aiCallLog);
        } catch (RuntimeException exception) {
            log.warn("Failed to save AI call log, userId={}, requestType={}, error={}",
                    currentUserId, REQUEST_TYPE_RECORD_ANALYZE, exception.getMessage());
        }
    }

    private String toJson(AiAnalyzeResultVO resultVO) {
        try {
            return objectMapper.writeValueAsString(resultVO);
        } catch (JsonProcessingException exception) {
            log.warn("Failed to serialize mock AI analyze result", exception);
            return "{}";
        }
    }

    private int elapsedMs(long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) elapsed;
    }
}
