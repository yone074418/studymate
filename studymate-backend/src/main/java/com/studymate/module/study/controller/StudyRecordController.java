package com.studymate.module.study.controller;

import com.studymate.common.Result;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.service.StudyRecordService;
import com.studymate.module.study.vo.StudyRecordDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Study records", description = "Current user's study record APIs")
@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {

    private final StudyRecordService studyRecordService;

    public StudyRecordController(StudyRecordService studyRecordService) {
        this.studyRecordService = studyRecordService;
    }

    @Operation(
            summary = "Create study record",
            description = "Requires login. Creates one study record for the current user parsed from Authorization Bearer Token.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public Result<StudyRecordDetailVO> createStudyRecord(@Valid @RequestBody StudyRecordCreateDTO createDTO) {
        return Result.success(studyRecordService.createStudyRecord(createDTO));
    }
}
