package com.studymate.module.study.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Study record list item")
public class StudyRecordVO {

    @Schema(description = "Study record ID", example = "1")
    private Long id;

    @Schema(description = "Study date", example = "2026-06-08")
    private LocalDate recordDate;

    @Schema(description = "Study duration in minutes", example = "90")
    private Integer durationMinutes;

    @Schema(description = "Study content summary", example = "Redis persistence")
    private String studyContent;

    @Schema(description = "Study category names", example = "[\"Redis\"]")
    private List<String> categories;

    @Schema(description = "Emotion status", example = "tired")
    private String emotionStatus;

    @Schema(description = "AI summary", example = "Reviewed Redis persistence.")
    private String aiSummary;

    @Schema(description = "Create time")
    private LocalDateTime createTime;
}
