package com.studymate.module.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "AI study record analyze request")
public class AiAnalyzeRequestDTO {

    @NotBlank(message = "rawContent cannot be blank")
    @Schema(description = "用户原始学习记录", example = "今天学了 Redis 持久化两个小时，有点累。", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rawContent;
}
