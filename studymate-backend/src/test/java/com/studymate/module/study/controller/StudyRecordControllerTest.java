package com.studymate.module.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.common.exception.GlobalExceptionHandler;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.dto.StudyRecordQueryDTO;
import com.studymate.module.study.dto.StudyRecordUpdateDTO;
import com.studymate.module.study.service.StudyRecordService;
import com.studymate.module.study.vo.StudyRecordDetailVO;
import com.studymate.module.study.vo.StudyRecordVO;
import com.studymate.security.JwtAuthenticationFilter;
import com.studymate.security.JwtProperties;
import com.studymate.security.JwtUtil;
import com.studymate.security.SecurityConfig;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyRecordController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class, JwtProperties.class})
class StudyRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private StudyRecordService studyRecordService;

    @Test
    void createAllowsValidTokenAndReturnsStudyRecordDetail() throws Exception {
        StudyRecordDetailVO detailVO = new StudyRecordDetailVO();
        detailVO.setId(11L);
        detailVO.setRecordDate(LocalDate.of(2026, 6, 8));
        detailVO.setRawContent("Studied Redis persistence for 90 minutes.");
        detailVO.setDurationMinutes(90);
        detailVO.setCategories(List.of("Redis"));
        detailVO.setWeakPoints(List.of("AOF and RDB comparison"));
        when(studyRecordService.createStudyRecord(any(StudyRecordCreateDTO.class))).thenReturn(detailVO);

        StudyRecordCreateDTO request = buildValidRequest();

        mockMvc.perform(post("/api/study-records")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(11))
                .andExpect(jsonPath("$.data.rawContent").value("Studied Redis persistence for 90 minutes."))
                .andExpect(jsonPath("$.data.categories[0]").value("Redis"))
                .andExpect(jsonPath("$.data.weakPoints[0]").value("AOF and RDB comparison"))
                .andExpect(jsonPath("$.data.userId").doesNotExist())
                .andExpect(jsonPath("$.data.deleted").doesNotExist());
    }

    @Test
    void createRejectsMissingTokenWithUnauthorizedResult() throws Exception {
        mockMvc.perform(post("/api/study-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void createRejectsBlankRawContent() throws Exception {
        StudyRecordCreateDTO request = buildValidRequest();
        request.setRawContent("");

        mockMvc.perform(post("/api/study-records")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void listAllowsValidTokenAndReturnsPagedStudyRecordVOs() throws Exception {
        StudyRecordVO recordVO = new StudyRecordVO();
        recordVO.setId(21L);
        recordVO.setRecordDate(LocalDate.of(2026, 6, 8));
        recordVO.setDurationMinutes(90);
        recordVO.setStudyContent("Redis persistence");
        recordVO.setCategories(List.of("Redis"));
        recordVO.setEmotionStatus("tired");
        Page<StudyRecordVO> page = new Page<>(1, 10, 1);
        page.setRecords(List.of(recordVO));
        when(studyRecordService.listStudyRecords(any(StudyRecordQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/api/study-records")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("recordDate", "2026-06-08")
                        .param("categoryName", "Redis")
                        .param("emotionStatus", "tired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(21))
                .andExpect(jsonPath("$.data.records[0].categories[0]").value("Redis"))
                .andExpect(jsonPath("$.data.records[0].deleted").doesNotExist());
    }

    @Test
    void detailAllowsValidTokenAndReturnsStudyRecordDetail() throws Exception {
        when(studyRecordService.getStudyRecordDetail(31L)).thenReturn(buildDetailVO(31L));

        mockMvc.perform(get("/api/study-records/31")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(31))
                .andExpect(jsonPath("$.data.categories[0]").value("Redis"))
                .andExpect(jsonPath("$.data.weakPoints[0]").value("AOF and RDB comparison"))
                .andExpect(jsonPath("$.data.userId").doesNotExist());
    }

    @Test
    void updateAllowsValidTokenAndReturnsUpdatedDetail() throws Exception {
        when(studyRecordService.updateStudyRecord(eq(41L), any(StudyRecordUpdateDTO.class))).thenReturn(buildDetailVO(41L));

        mockMvc.perform(put("/api/study-records/41")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(41))
                .andExpect(jsonPath("$.data.weakPoints[0]").value("AOF and RDB comparison"));
    }

    @Test
    void deleteAllowsValidTokenAndReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/study-records/51")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private StudyRecordCreateDTO buildValidRequest() {
        StudyRecordCreateDTO request = new StudyRecordCreateDTO();
        request.setRecordDate(LocalDate.of(2026, 6, 8));
        request.setRawContent("Studied Redis persistence for 90 minutes.");
        request.setDurationMinutes(90);
        request.setStudyContent("Redis persistence");
        request.setCategories(List.of("Redis"));
        request.setWeakPoints(List.of("AOF and RDB comparison"));
        request.setEmotionStatus("tired");
        request.setTomorrowPlan("Make a comparison table.");
        request.setAiSummary("Reviewed Redis persistence.");
        request.setAiComfort("Noticing the gap is progress.");
        request.setRemark("Need review.");
        return request;
    }

    private StudyRecordUpdateDTO buildUpdateRequest() {
        StudyRecordUpdateDTO request = new StudyRecordUpdateDTO();
        request.setRecordDate(LocalDate.of(2026, 6, 9));
        request.setRawContent("Updated Redis note.");
        request.setDurationMinutes(120);
        request.setStudyContent("Updated Redis persistence");
        request.setCategories(List.of("Redis"));
        request.setWeakPoints(List.of("AOF and RDB comparison"));
        request.setEmotionStatus("calm");
        request.setTomorrowPlan("Review rewrite.");
        request.setAiSummary("Updated summary.");
        request.setAiComfort("Small progress counts.");
        request.setRemark("Updated.");
        return request;
    }

    private StudyRecordDetailVO buildDetailVO(Long id) {
        StudyRecordDetailVO detailVO = new StudyRecordDetailVO();
        detailVO.setId(id);
        detailVO.setRecordDate(LocalDate.of(2026, 6, 8));
        detailVO.setRawContent("Studied Redis persistence for 90 minutes.");
        detailVO.setDurationMinutes(90);
        detailVO.setStudyContent("Redis persistence");
        detailVO.setCategories(List.of("Redis"));
        detailVO.setWeakPoints(List.of("AOF and RDB comparison"));
        detailVO.setEmotionStatus("tired");
        return detailVO;
    }
}
