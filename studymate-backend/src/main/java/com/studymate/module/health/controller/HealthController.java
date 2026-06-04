package com.studymate.module.health.controller;

import com.studymate.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "健康检查")
@RestController
public class HealthController {

    @Operation(summary = "检查后端服务是否正常")
    @GetMapping("/api/health")
    public Result<String> health() {
        return Result.success("StudyMate backend is running");
    }
}
