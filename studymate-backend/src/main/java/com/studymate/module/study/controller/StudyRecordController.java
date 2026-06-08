package com.studymate.module.study.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studymate.common.Result;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.dto.StudyRecordQueryDTO;
import com.studymate.module.study.dto.StudyRecordUpdateDTO;
import com.studymate.module.study.service.StudyRecordService;
import com.studymate.module.study.vo.StudyRecordDetailVO;
import com.studymate.module.study.vo.StudyRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Study records", description = "Current user's study record APIs")
@RestController
@RequestMapping("/api/study-records")
@Validated
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

    @Operation(
            summary = "List study records",
            description = "Requires login. Returns paged study records owned by the current user. Supports recordDate, categoryName and emotionStatus filters.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public Result<IPage<StudyRecordVO>> listStudyRecords(@Valid @ModelAttribute StudyRecordQueryDTO queryDTO) {
        return Result.success(studyRecordService.listStudyRecords(queryDTO));
    }

    @Operation(
            summary = "Get study record detail",
            description = "Requires login. Returns one study record detail only when it belongs to the current user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    public Result<StudyRecordDetailVO> getStudyRecordDetail(@PathVariable Long id) {
        return Result.success(studyRecordService.getStudyRecordDetail(id));
    }

    @Operation(
            summary = "Update study record",
            description = "Requires login. Updates one study record owned by the current user and rebuilds categories and weak points.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    public Result<StudyRecordDetailVO> updateStudyRecord(@PathVariable Long id, @Valid @RequestBody StudyRecordUpdateDTO updateDTO) {
        return Result.success(studyRecordService.updateStudyRecord(id, updateDTO));
    }

    @Operation(
            summary = "Delete study record",
            description = "Requires login. Logically deletes one study record owned by the current user and removes related data from later statistics.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudyRecord(@PathVariable Long id) {
        studyRecordService.deleteStudyRecord(id);
        return Result.success();
    }
}
