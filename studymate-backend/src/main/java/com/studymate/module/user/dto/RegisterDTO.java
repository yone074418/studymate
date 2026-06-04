package com.studymate.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户注册请求")
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名，注册后用于登录", example = "java_rookie", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱，注册后也可用于登录", example = "java_rookie@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码，后端会使用 BCrypt 加密保存", example = "StudyMate123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
