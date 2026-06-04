package com.studymate.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户登录请求")
public class LoginDTO {

    @NotBlank(message = "用户名或邮箱不能为空")
    @Schema(description = "用户名或邮箱", example = "java_rookie", requiredMode = Schema.RequiredMode.REQUIRED)
    private String usernameOrEmail;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码", example = "StudyMate123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
