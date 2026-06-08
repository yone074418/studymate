package com.studymate.module.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "AI study record analyze result")
public class AiAnalyzeResultVO {

    @Schema(description = "学习时长，单位分钟", example = "120")
    private Integer durationMinutes;

    @Schema(description = "学习方向数组", example = "[\"Redis\"]")
    private List<String> categories;

    @Schema(description = "学习内容总结", example = "今天主要学习了 Redis 持久化相关内容。")
    private String studyContent;

    @Schema(description = "薄弱点数组", example = "[\"AOF 和 RDB 的区别还不够清楚\"]")
    private List<String> weakPoints;

    @Schema(description = "情绪状态", example = "有点累")
    private String emotionStatus;

    @Schema(description = "明日计划", example = "明天可以先整理一张 AOF 和 RDB 的对比表。")
    private String tomorrowPlan;

    @Schema(description = "AI 总结", example = "今天学习了 Redis 持久化相关内容，并发现了需要继续巩固的薄弱点。")
    private String aiSummary;

    @Schema(description = "安慰反馈", example = "能发现自己哪里不懂，本身就是一种进步。今天已经做得不错了。")
    private String aiComfort;
}
