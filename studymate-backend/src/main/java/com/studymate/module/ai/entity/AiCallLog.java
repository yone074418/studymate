package com.studymate.module.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_call_log")
public class AiCallLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long studyRecordId;

    private String requestType;

    private String modelName;

    private String prompt;

    private String requestContent;

    private String responseContent;

    private Integer success;

    private String errorMessage;

    private Integer durationMs;

    private LocalDateTime createTime;
}
