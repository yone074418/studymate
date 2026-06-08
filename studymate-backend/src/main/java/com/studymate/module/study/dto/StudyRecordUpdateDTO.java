package com.studymate.module.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Study record update request")
public class StudyRecordUpdateDTO {

    @NotNull(message = "recordDate cannot be null")
    @Schema(description = "Study date", example = "2026-06-08", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate recordDate;

    @NotBlank(message = "rawContent cannot be blank")
    @Schema(description = "Raw user study input", example = "Studied Redis persistence for 90 minutes.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rawContent;

    @Min(value = 0, message = "durationMinutes cannot be negative")
    @Schema(description = "Study duration in minutes", example = "90")
    private Integer durationMinutes;

    @Schema(description = "Study content summary", example = "Redis persistence")
    private String studyContent;

    @Schema(description = "Study category names", example = "[\"Redis\", \"MySQL\"]")
    private List<String> categories;

    @Schema(description = "Weak point descriptions", example = "[\"AOF and RDB comparison\"]")
    private List<String> weakPoints;

    @Schema(description = "Emotion status", example = "tired")
    private String emotionStatus;

    @Schema(description = "Tomorrow plan", example = "Make a comparison table.")
    private String tomorrowPlan;

    @Schema(description = "AI summary", example = "Reviewed Redis persistence.")
    private String aiSummary;

    @Schema(description = "AI comfort feedback", example = "Noticing the gap is progress.")
    private String aiComfort;

    @Schema(description = "User remark", example = "Need review.")
    private String remark;
}
