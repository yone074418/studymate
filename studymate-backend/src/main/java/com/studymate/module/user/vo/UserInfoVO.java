package com.studymate.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户基础信息")
public class UserInfoVO {

    @Schema(description = "用户 ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "java_rookie")
    private String username;

    @Schema(description = "邮箱", example = "java_rookie@example.com")
    private String email;

    @Schema(description = "昵称", example = "Java 实习备考生")
    private String nickname;

    @Schema(description = "头像 URL", example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "目标岗位", example = "Java 后端实习生")
    private String targetPosition;

    @Schema(description = "每日目标学习分钟数", example = "120")
    private Integer dailyTargetMinutes;

    @Schema(description = "学习阶段", example = "基础复习")
    private String studyStage;
}
