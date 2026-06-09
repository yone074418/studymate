package com.studymate.module.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "情绪趋势")
public class EmotionTrendVO {

    @Schema(description = "日期", example = "2026-06-09")
    private LocalDate date;

    @Schema(description = "当天最近一条学习记录的情绪状态", example = "平静")
    private String emotionStatus;
}
