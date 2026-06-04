package com.studymate.common.exception;

import com.studymate.enums.ResultCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(GlobalExceptionHandler.class)
@ContextConfiguration(classes = GlobalExceptionHandlerTest.TestController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handlesBusinessExceptionWithUnifiedResult() throws Exception {
        mockMvc.perform(post("/test/business-error"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void handlesValidationExceptionWithFriendlyMessage() throws Exception {
        mockMvc.perform(post("/test/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("用户名不能为空")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @RestController
    static class TestController {

        @PostMapping("/test/business-error")
        void businessError() {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        @PostMapping("/test/validation-error")
        void validationError(@Valid @RequestBody TestRequest request) {
        }
    }

    static class TestRequest {

        @NotBlank(message = "用户名不能为空")
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
