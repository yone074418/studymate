package com.studymate.module.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.common.exception.GlobalExceptionHandler;
import com.studymate.module.ai.dto.AiAnalyzeRequestDTO;
import com.studymate.module.ai.service.AiRecordAnalyzeService;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;
import com.studymate.security.JwtAuthenticationFilter;
import com.studymate.security.JwtProperties;
import com.studymate.security.JwtUtil;
import com.studymate.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiRecordController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class, JwtProperties.class})
class AiRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private AiRecordAnalyzeService aiRecordAnalyzeService;

    @Test
    void analyzeAllowsValidTokenAndReturnsCompleteMockResult() throws Exception {
        when(aiRecordAnalyzeService.analyze(eq(1L), any(AiAnalyzeRequestDTO.class))).thenReturn(buildMockResult());

        AiAnalyzeRequestDTO request = new AiAnalyzeRequestDTO();
        request.setRawContent("今天学了 Redis 持久化两个小时，有点累。");

        mockMvc.perform(post("/api/ai/record/analyze")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.durationMinutes").value(120))
                .andExpect(jsonPath("$.data.categories[0]").value("Redis"))
                .andExpect(jsonPath("$.data.studyContent").value("今天主要学习了 Redis 持久化相关内容。"))
                .andExpect(jsonPath("$.data.weakPoints[0]").value("AOF 和 RDB 的区别还不够清楚"))
                .andExpect(jsonPath("$.data.emotionStatus").value("有点累"))
                .andExpect(jsonPath("$.data.tomorrowPlan").value("明天可以先整理一张 AOF 和 RDB 的对比表。"))
                .andExpect(jsonPath("$.data.aiSummary").value("今天学习了 Redis 持久化相关内容，并发现了需要继续巩固的薄弱点。"))
                .andExpect(jsonPath("$.data.aiComfort").value("能发现自己哪里不懂，本身就是一种进步。今天已经做得不错了。"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.userId").doesNotExist());
    }

    @Test
    void analyzeRejectsBlankRawContent() throws Exception {
        AiAnalyzeRequestDTO request = new AiAnalyzeRequestDTO();
        request.setRawContent("");

        mockMvc.perform(post("/api/ai/record/analyze")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void analyzeRejectsMissingTokenWithUnauthorizedResult() throws Exception {
        AiAnalyzeRequestDTO request = new AiAnalyzeRequestDTO();
        request.setRawContent("今天学了 Redis。");

        mockMvc.perform(post("/api/ai/record/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
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
}
