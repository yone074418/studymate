package com.studymate.module.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studymate.module.statistics.vo.CategoryStatisticVO;
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

    @Select("""
            SELECT
                category_stats.category_name AS categoryName,
                category_stats.record_count AS recordCount,
                category_stats.duration_minutes AS durationMinutes,
                CASE
                    WHEN total_stats.total_duration_minutes = 0 THEN 0
                    ELSE ROUND(category_stats.duration_minutes * 100.0 / total_stats.total_duration_minutes, 2)
                END AS percentage
            FROM (
                SELECT
                    c.name AS category_name,
                    COUNT(DISTINCT sr.id) AS record_count,
                    COALESCE(SUM(sr.duration_minutes), 0) AS duration_minutes
                FROM study_record_category rc
                INNER JOIN study_record sr ON rc.study_record_id = sr.id
                INNER JOIN study_category c ON rc.category_id = c.id
                WHERE rc.user_id = #{userId}
                  AND sr.user_id = #{userId}
                  AND sr.deleted = 0
                  AND c.status = 1
                GROUP BY c.id, c.name
            ) category_stats
            CROSS JOIN (
                SELECT COALESCE(SUM(sr.duration_minutes), 0) AS total_duration_minutes
                FROM study_record_category rc
                INNER JOIN study_record sr ON rc.study_record_id = sr.id
                INNER JOIN study_category c ON rc.category_id = c.id
                WHERE rc.user_id = #{userId}
                  AND sr.user_id = #{userId}
                  AND sr.deleted = 0
                  AND c.status = 1
            ) total_stats
            ORDER BY category_stats.duration_minutes DESC, category_stats.record_count DESC, category_stats.category_name ASC
            """)
    List<CategoryStatisticVO> selectCategoryStatistics(@Param("userId") Long userId);
}
