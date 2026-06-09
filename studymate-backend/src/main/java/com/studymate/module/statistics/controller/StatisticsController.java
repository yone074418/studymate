package com.studymate.module.statistics.controller;

import com.studymate.common.Result;
import com.studymate.module.statistics.service.StatisticsService;
import com.studymate.module.statistics.vo.CategoryStatisticVO;
import com.studymate.module.statistics.vo.DashboardVO;
import com.studymate.module.statistics.vo.EmotionTrendVO;
import com.studymate.module.statistics.vo.StudyTrendVO;
import com.studymate.module.statistics.vo.WeakPointRankVO;
import com.studymate.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Statistics", description = "Current user's study statistics APIs")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(
            summary = "Get dashboard statistics",
            description = "Requires login. Returns dashboard aggregate data for the current user parsed from Authorization Bearer Token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        return Result.success(statisticsService.getDashboard(SecurityUtils.getCurrentUserId()));
    }

    @Operation(
            summary = "Get study duration trend",
            description = "Requires login. Returns the current user's last 7 days study duration trend and fills days without data with 0.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/trend")
    public Result<List<StudyTrendVO>> getTrend() {
        return Result.success(statisticsService.getTrend(SecurityUtils.getCurrentUserId()));
    }

    @Operation(
            summary = "Get category statistics",
            description = "Requires login. Returns the current user's study category duration and record count proportions.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/category")
    public Result<List<CategoryStatisticVO>> getCategoryStatistics() {
        return Result.success(statisticsService.getCategoryStatistics(SecurityUtils.getCurrentUserId()));
    }

    @Operation(
            summary = "Get weak point ranking",
            description = "Requires login. Returns the current user's weak point ranking sorted by occurrence count descending.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/weak-points")
    public Result<List<WeakPointRankVO>> getWeakPointRank() {
        return Result.success(statisticsService.getWeakPointRank(SecurityUtils.getCurrentUserId()));
    }

    @Operation(
            summary = "Get emotion trend",
            description = "Requires login. Returns the current user's last 7 days emotion trend using the latest record of each day.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/emotion")
    public Result<List<EmotionTrendVO>> getEmotionTrend() {
        return Result.success(statisticsService.getEmotionTrend(SecurityUtils.getCurrentUserId()));
    }
}
