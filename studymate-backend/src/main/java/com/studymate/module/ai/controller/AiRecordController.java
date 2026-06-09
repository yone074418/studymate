package com.studymate.module.ai.controller;

import com.studymate.common.Result;
import com.studymate.module.ai.dto.AiAnalyzeRequestDTO;
import com.studymate.module.ai.service.AiRecordAnalyzeService;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;
import com.studymate.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI record analyze", description = "AI study record analyze APIs")
@RestController
@RequestMapping("/api/ai/record")
@Validated
public class AiRecordController {

    private final AiRecordAnalyzeService aiRecordAnalyzeService;

    public AiRecordController(AiRecordAnalyzeService aiRecordAnalyzeService) {
        this.aiRecordAnalyzeService = aiRecordAnalyzeService;
    }

    @Operation(
            summary = "Analyze study record with AI",
            description = "Requires login and Bearer Token. Supports mock mode and real AI mode, returns a structured analyze result, and writes one AI call log for the current user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/analyze")
    public Result<AiAnalyzeResultVO> analyze(@Valid @RequestBody AiAnalyzeRequestDTO requestDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return Result.success(aiRecordAnalyzeService.analyze(currentUserId, requestDTO));
    }
}
