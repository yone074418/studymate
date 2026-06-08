package com.studymate.module.study.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Study record detail")
public class StudyRecordDetailVO extends StudyRecordVO {

    @Schema(description = "Raw user study input", example = "Studied Redis persistence for 90 minutes.")
    private String rawContent;

    @Schema(description = "Weak point descriptions", example = "[\"AOF and RDB comparison\"]")
    private List<String> weakPoints;

    @Schema(description = "Tomorrow plan", example = "Make a comparison table.")
    private String tomorrowPlan;

    @Schema(description = "AI comfort feedback", example = "Noticing the gap is progress.")
    private String aiComfort;

    @Schema(description = "User remark", example = "Need review.")
    private String remark;

    @Schema(description = "Update time")
    private LocalDateTime updateTime;
}
