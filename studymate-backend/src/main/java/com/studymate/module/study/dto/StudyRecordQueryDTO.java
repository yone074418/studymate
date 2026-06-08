package com.studymate.module.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "Study record list query")
public class StudyRecordQueryDTO {

    @Min(value = 1, message = "pageNum must be greater than 0")
    @Schema(description = "Page number, starting from 1", example = "1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "pageSize must be greater than 0")
    @Schema(description = "Page size", example = "10")
    private Long pageSize = 10L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Study date filter", example = "2026-06-08")
    private LocalDate recordDate;

    @Schema(description = "Study category name filter", example = "Redis")
    private String categoryName;

    @Schema(description = "Emotion status filter", example = "tired")
    private String emotionStatus;
}
