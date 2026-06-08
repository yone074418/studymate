package com.studymate.module.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_record_category")
public class StudyRecordCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studyRecordId;

    private Long categoryId;

    private Long userId;

    private LocalDateTime createTime;
}
