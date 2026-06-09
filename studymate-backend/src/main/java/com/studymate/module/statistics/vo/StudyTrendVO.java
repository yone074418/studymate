package com.studymate.module.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学习时长趋势")
public class StudyTrendVO {

    @Schema(description = "日期", example = "2026-06-09")
    private LocalDate date;

    @Schema(description = "当天学习时长，单位分钟", example = "120")
    private Integer durationMinutes;
}
