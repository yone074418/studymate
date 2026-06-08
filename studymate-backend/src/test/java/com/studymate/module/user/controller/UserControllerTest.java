package com.studymate.module.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.common.exception.GlobalExceptionHandler;
import com.studymate.module.user.dto.UserProfileUpdateDTO;
import com.studymate.module.user.service.UserProfileService;
import com.studymate.module.user.vo.UserInfoVO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class, JwtAuthenticationFilter.class, JwtUtil.class, JwtProperties.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private UserProfileService userProfileService;

    @Test
    void profileAllowsValidTokenAndDoesNotReturnPassword() throws Exception {
        when(userProfileService.getCurrentUserProfile())
                .thenReturn(new UserInfoVO(1L, "alice", "alice@example.com", "Alice", null, "Java Intern", 120, "basic"));

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void profileRejectsMissingTokenWithUnauthorizedResult() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void profileRejectsInvalidTokenWithUnauthorizedResult() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void updateProfileAllowsEditableFieldsAndDoesNotReturnPassword() throws Exception {
        UserProfileUpdateDTO request = new UserProfileUpdateDTO();
        request.setNickname("New Alice");
        request.setTargetPosition("Java Backend Intern");
        request.setDailyTargetMinutes(150);

        when(userProfileService.updateCurrentUserProfile(any(UserProfileUpdateDTO.class)))
                .thenReturn(new UserInfoVO(1L, "alice", "alice@example.com", "New Alice", null, "Java Backend Intern", 150, null));

        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(1L, "alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("New Alice"))
                .andExpect(jsonPath("$.data.targetPosition").value("Java Backend Intern"))
                .andExpect(jsonPath("$.data.dailyTargetMinutes").value(150))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }
}
