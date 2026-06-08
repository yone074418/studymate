package com.studymate.module.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studymate.module.study.entity.StudyRecordCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudyRecordCategoryMapper extends BaseMapper<StudyRecordCategory> {

    @Select("SELECT id FROM study_category WHERE name = #{name} AND status = 1 LIMIT 1")
    Long selectCategoryIdByName(@Param("name") String name);

    @Select("""
            SELECT c.name
            FROM study_record_category rc
            INNER JOIN study_category c ON rc.category_id = c.id
            WHERE rc.study_record_id = #{studyRecordId}
              AND rc.user_id = #{userId}
              AND c.status = 1
            ORDER BY rc.id ASC
            """)
    List<String> selectCategoryNamesByRecordId(@Param("studyRecordId") Long studyRecordId, @Param("userId") Long userId);
}
