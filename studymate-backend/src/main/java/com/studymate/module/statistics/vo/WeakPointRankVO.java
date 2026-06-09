package com.studymate.module.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "薄弱点排行")
public class WeakPointRankVO {

    @Schema(description = "薄弱点内容", example = "AOF 和 RDB 对比")
    private String content;

    @Schema(description = "出现次数", example = "3")
    private Integer count;
}
