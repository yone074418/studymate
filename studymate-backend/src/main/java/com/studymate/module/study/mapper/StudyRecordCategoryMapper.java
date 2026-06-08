package com.studymate.module.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studymate.module.study.entity.StudyRecordCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudyRecordCategoryMapper extends BaseMapper<StudyRecordCategory> {

    @Select("SELECT id FROM study_category WHERE name = #{name} AND status = 1 LIMIT 1")
    Long selectCategoryIdByName(@Param("name") String name);
}
