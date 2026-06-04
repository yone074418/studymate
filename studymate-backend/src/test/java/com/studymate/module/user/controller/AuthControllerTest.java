package com.studymate.module.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.common.exception.GlobalExceptionHandler;
import com.studymate.module.user.dto.LoginDTO;
import com.studymate.module.user.dto.RegisterDTO;
import com.studymate.module.user.service.UserAuthService;
import com.studymate.module.user.vo.LoginVO;
import com.studymate.module.user.vo.UserInfoVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserAuthService userAuthService;

    @Test
    void registerReturnsUnifiedUserInfoResponse() throws Exception {
        when(userAuthService.register(any(RegisterDTO.class))).thenReturn(new UserInfoVO(1L, "alice", "alice@example.com", null, null, null, 120, null));
        RegisterDTO request = new RegisterDTO();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("plain123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void registerRejectsBlankPassword() throws Exception {
        RegisterDTO request = new RegisterDTO();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码不能为空"));
    }

    @Test
    void loginReturnsUnifiedTokenResponse() throws Exception {
        UserInfoVO userInfo = new UserInfoVO(1L, "alice", "alice@example.com", null, null, null, 120, null);
        when(userAuthService.login(any(LoginDTO.class))).thenReturn(new LoginVO("jwt-token", userInfo));
        LoginDTO request = new LoginDTO();
        request.setUsernameOrEmail("alice");
        request.setPassword("plain123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.userInfo.username").value("alice"));
    }
}
