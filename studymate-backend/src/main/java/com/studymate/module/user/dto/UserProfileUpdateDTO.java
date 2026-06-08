package com.studymate.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户个人信息修改请求")
public class UserProfileUpdateDTO {

    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "Java实习备考生")
    private String nickname;

    @Size(max = 100, message = "目标岗位长度不能超过100个字符")
    @Schema(description = "目标岗位", example = "Java后端实习生")
    private String targetPosition;

    @Min(value = 0, message = "每日目标学习分钟数不能小于0")
    @Max(value = 1440, message = "每日目标学习分钟数不能超过1440")
    @Schema(description = "每日目标学习分钟数", example = "120")
    private Integer dailyTargetMinutes;

    @Size(max = 50, message = "学习阶段长度不能超过50个字符")
    @Schema(description = "学习阶段", example = "基础复习")
    private String studyStage;

    @Size(max = 500, message = "头像 URL 长度不能超过500个字符")
    @Schema(description = "头像 URL", example = "https://example.com/avatar.png")
    private String avatarUrl;
}
