package com.studymate.module.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("weak_point")
public class WeakPoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long studyRecordId;

    private Long categoryId;

    private String content;

    private Integer resolved;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
