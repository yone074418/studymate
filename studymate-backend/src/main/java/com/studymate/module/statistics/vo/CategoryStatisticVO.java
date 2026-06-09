package com.studymate.module.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学习方向统计")
public class CategoryStatisticVO {

    @Schema(description = "学习方向名称", example = "Redis")
    private String categoryName;

    @Schema(description = "关联记录数量", example = "2")
    private Integer recordCount;

    @Schema(description = "该方向累计学习时长，单位分钟", example = "180")
    private Integer durationMinutes;

    @Schema(description = "该方向时长占比", example = "45.5")
    private Double percentage;
}
