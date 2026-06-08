package com.studymate.module.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studymate.module.study.entity.WeakPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WeakPointMapper extends BaseMapper<WeakPoint> {

    @Select("""
            SELECT content
            FROM weak_point
            WHERE study_record_id = #{studyRecordId}
              AND user_id = #{userId}
              AND deleted = 0
            ORDER BY id ASC
            """)
    List<String> selectContentsByRecordId(@Param("studyRecordId") Long studyRecordId, @Param("userId") Long userId);
}
