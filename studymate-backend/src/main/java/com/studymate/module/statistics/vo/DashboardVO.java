package com.studymate.module.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "首页仪表盘统计数据")
public class DashboardVO {

    @Schema(description = "今日学习时长，单位分钟", example = "90")
    private Integer todayDurationMinutes;

    @Schema(description = "本周学习时长，单位分钟", example = "420")
    private Integer weekDurationMinutes;

    @Schema(description = "本月学习时长，单位分钟", example = "1260")
    private Integer monthDurationMinutes;

    @Schema(description = "累计学习时长，单位分钟", example = "3600")
    private Integer totalDurationMinutes;

    @Schema(description = "连续学习天数", example = "5")
    private Integer continuousStudyDays;

    @Schema(description = "最近薄弱点排行")
    private List<WeakPointRankVO> recentWeakPoints;

    @Schema(description = "最近一条学习记录的情绪状态", example = "平静")
    private String recentEmotionStatus;

    @Schema(description = "最近 7 天学习时长趋势")
    private List<StudyTrendVO> recentTrend;
}
