package com.studymate.module.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.studymate.common.exception.BusinessException;
import com.studymate.enums.ResultCode;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.dto.StudyRecordQueryDTO;
import com.studymate.module.study.dto.StudyRecordUpdateDTO;
import com.studymate.module.study.entity.StudyRecord;
import com.studymate.module.study.entity.StudyRecordCategory;
import com.studymate.module.study.entity.WeakPoint;
import com.studymate.module.study.mapper.StudyRecordCategoryMapper;
import com.studymate.module.study.mapper.StudyRecordMapper;
import com.studymate.module.study.mapper.WeakPointMapper;
import com.studymate.module.study.service.StudyRecordService;
import com.studymate.module.study.vo.StudyRecordDetailVO;
import com.studymate.module.study.vo.StudyRecordVO;
import com.studymate.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudyRecordServiceImpl implements StudyRecordService {

    private static final int DEFAULT_DURATION_MINUTES = 0;
    private static final int NOT_DELETED = 0;
    private static final int UNRESOLVED = 0;

    private final StudyRecordMapper studyRecordMapper;
    private final StudyRecordCategoryMapper studyRecordCategoryMapper;
    private final WeakPointMapper weakPointMapper;

    public StudyRecordServiceImpl(
            StudyRecordMapper studyRecordMapper,
            StudyRecordCategoryMapper studyRecordCategoryMapper,
            WeakPointMapper weakPointMapper
    ) {
        this.studyRecordMapper = studyRecordMapper;
        this.studyRecordCategoryMapper = studyRecordCategoryMapper;
        this.weakPointMapper = weakPointMapper;
    }

    @Override
    @Transactional
    public StudyRecordDetailVO createStudyRecord(StudyRecordCreateDTO createDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        rejectDuplicate(currentUserId, createDTO);

        LocalDateTime now = LocalDateTime.now();
        StudyRecord studyRecord = buildStudyRecord(currentUserId, createDTO, now);
        studyRecordMapper.insert(studyRecord);

        List<String> savedCategories = saveCategories(currentUserId, studyRecord.getId(), createDTO.getCategories(), now);
        List<String> savedWeakPoints = saveWeakPoints(currentUserId, studyRecord.getId(), createDTO.getWeakPoints(), now);
        return toDetailVO(studyRecord, savedCategories, savedWeakPoints);
    }

    @Override
    public IPage<StudyRecordVO> listStudyRecords(StudyRecordQueryDTO queryDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        StudyRecordQueryDTO safeQuery = queryDTO == null ? new StudyRecordQueryDTO() : queryDTO;
        Page<StudyRecord> page = new Page<>(safeQuery.getPageNum(), safeQuery.getPageSize());

        LambdaQueryWrapper<StudyRecord> queryWrapper = new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getUserId, currentUserId)
                .eq(StudyRecord::getDeleted, NOT_DELETED)
                .eq(safeQuery.getRecordDate() != null, StudyRecord::getRecordDate, safeQuery.getRecordDate())
                .eq(hasText(safeQuery.getEmotionStatus()), StudyRecord::getEmotionStatus, trim(safeQuery.getEmotionStatus()))
                .apply(hasText(safeQuery.getCategoryName()),
                        """
                                EXISTS (
                                    SELECT 1
                                    FROM study_record_category src
                                    INNER JOIN study_category sc ON src.category_id = sc.id
                                    WHERE src.study_record_id = study_record.id
                                      AND src.user_id = {0}
                                      AND sc.name = {1}
                                      AND sc.status = 1
                                )
                                """,
                        currentUserId,
                        trim(safeQuery.getCategoryName()))
                .orderByDesc(StudyRecord::getRecordDate)
                .orderByDesc(StudyRecord::getCreateTime);

        IPage<StudyRecord> recordPage = studyRecordMapper.selectPage(page, queryWrapper);
        Page<StudyRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(recordPage.getRecords().stream()
                .map(record -> toListVO(record, loadCategories(record.getId(), currentUserId)))
                .toList());
        return voPage;
    }

    @Override
    public StudyRecordDetailVO getStudyRecordDetail(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        StudyRecord studyRecord = getOwnedActiveRecord(id, currentUserId);
        return toDetailVO(studyRecord, loadCategories(id, currentUserId), loadWeakPoints(id, currentUserId));
    }

    @Override
    @Transactional
    public StudyRecordDetailVO updateStudyRecord(Long id, StudyRecordUpdateDTO updateDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        StudyRecord studyRecord = getOwnedActiveRecord(id, currentUserId);
        LocalDateTime now = LocalDateTime.now();

        applyUpdate(studyRecord, updateDTO, now);
        studyRecordMapper.updateById(studyRecord);

        rebuildCategories(currentUserId, id, updateDTO.getCategories(), now);
        rebuildWeakPoints(currentUserId, id, updateDTO.getWeakPoints(), now);

        return getStudyRecordDetail(id);
    }

    @Override
    @Transactional
    public void deleteStudyRecord(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        getOwnedActiveRecord(id, currentUserId);
        LocalDateTime now = LocalDateTime.now();

        studyRecordMapper.update(null, new UpdateWrapper<StudyRecord>()
                .eq("id", id)
                .eq("user_id", currentUserId)
                .eq("deleted", NOT_DELETED)
                .set("deleted", 1)
                .set("update_time", now));
        removeWeakPoints(currentUserId, id);
        removeCategories(currentUserId, id);
    }

    private void rejectDuplicate(Long currentUserId, StudyRecordCreateDTO createDTO) {
        StudyRecord existing = studyRecordMapper.selectOne(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getUserId, currentUserId)
                .eq(StudyRecord::getRecordDate, createDTO.getRecordDate())
                .eq(StudyRecord::getRawContent, createDTO.getRawContent())
                .eq(StudyRecord::getDeleted, NOT_DELETED)
                .last("LIMIT 1"));
        if (existing != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "Study record already exists");
        }
    }

    private StudyRecord buildStudyRecord(Long currentUserId, StudyRecordCreateDTO createDTO, LocalDateTime now) {
        StudyRecord studyRecord = new StudyRecord();
        studyRecord.setUserId(currentUserId);
        studyRecord.setRecordDate(createDTO.getRecordDate());
        studyRecord.setRawContent(createDTO.getRawContent());
        studyRecord.setDurationMinutes(createDTO.getDurationMinutes() == null ? DEFAULT_DURATION_MINUTES : createDTO.getDurationMinutes());
        studyRecord.setStudyContent(createDTO.getStudyContent());
        studyRecord.setEmotionStatus(createDTO.getEmotionStatus());
        studyRecord.setTomorrowPlan(createDTO.getTomorrowPlan());
        studyRecord.setAiSummary(createDTO.getAiSummary());
        studyRecord.setAiComfort(createDTO.getAiComfort());
        studyRecord.setRemark(createDTO.getRemark());
        studyRecord.setDeleted(NOT_DELETED);
        studyRecord.setCreateTime(now);
        studyRecord.setUpdateTime(now);
        return studyRecord;
    }

    private List<String> saveCategories(Long currentUserId, Long studyRecordId, List<String> categories, LocalDateTime now) {
        List<String> savedCategories = new ArrayList<>();
        for (String categoryName : normalize(categories)) {
            Long categoryId = studyRecordCategoryMapper.selectCategoryIdByName(categoryName);
            if (categoryId == null) {
                continue;
            }
            StudyRecordCategory relation = new StudyRecordCategory();
            relation.setStudyRecordId(studyRecordId);
            relation.setCategoryId(categoryId);
            relation.setUserId(currentUserId);
            relation.setCreateTime(now);
            studyRecordCategoryMapper.insert(relation);
            savedCategories.add(categoryName);
        }
        return savedCategories;
    }

    private List<String> saveWeakPoints(Long currentUserId, Long studyRecordId, List<String> weakPoints, LocalDateTime now) {
        List<String> savedWeakPoints = new ArrayList<>();
        for (String weakPointContent : normalize(weakPoints)) {
            WeakPoint weakPoint = new WeakPoint();
            weakPoint.setUserId(currentUserId);
            weakPoint.setStudyRecordId(studyRecordId);
            weakPoint.setContent(weakPointContent);
            weakPoint.setResolved(UNRESOLVED);
            weakPoint.setDeleted(NOT_DELETED);
            weakPoint.setCreateTime(now);
            weakPoint.setUpdateTime(now);
            weakPointMapper.insert(weakPoint);
            savedWeakPoints.add(weakPointContent);
        }
        return savedWeakPoints;
    }

    private void rebuildCategories(Long currentUserId, Long studyRecordId, List<String> categories, LocalDateTime now) {
        removeCategories(currentUserId, studyRecordId);
        saveCategories(currentUserId, studyRecordId, categories, now);
    }

    private void rebuildWeakPoints(Long currentUserId, Long studyRecordId, List<String> weakPoints, LocalDateTime now) {
        removeWeakPoints(currentUserId, studyRecordId);
        saveWeakPoints(currentUserId, studyRecordId, weakPoints, now);
    }

    private void removeCategories(Long currentUserId, Long studyRecordId) {
        studyRecordCategoryMapper.delete(new LambdaQueryWrapper<StudyRecordCategory>()
                .eq(StudyRecordCategory::getStudyRecordId, studyRecordId)
                .eq(StudyRecordCategory::getUserId, currentUserId));
    }

    private void removeWeakPoints(Long currentUserId, Long studyRecordId) {
        weakPointMapper.delete(new LambdaQueryWrapper<WeakPoint>()
                .eq(WeakPoint::getStudyRecordId, studyRecordId)
                .eq(WeakPoint::getUserId, currentUserId)
                .eq(WeakPoint::getDeleted, NOT_DELETED));
    }

    private StudyRecord getOwnedActiveRecord(Long id, Long currentUserId) {
        StudyRecord studyRecord = studyRecordMapper.selectOne(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getId, id)
                .eq(StudyRecord::getUserId, currentUserId)
                .eq(StudyRecord::getDeleted, NOT_DELETED)
                .last("LIMIT 1"));
        if (studyRecord == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "Study record not found");
        }
        return studyRecord;
    }

    private void applyUpdate(StudyRecord studyRecord, StudyRecordUpdateDTO updateDTO, LocalDateTime now) {
        studyRecord.setRecordDate(updateDTO.getRecordDate());
        studyRecord.setRawContent(updateDTO.getRawContent());
        studyRecord.setDurationMinutes(updateDTO.getDurationMinutes() == null ? DEFAULT_DURATION_MINUTES : updateDTO.getDurationMinutes());
        studyRecord.setStudyContent(updateDTO.getStudyContent());
        studyRecord.setEmotionStatus(updateDTO.getEmotionStatus());
        studyRecord.setTomorrowPlan(updateDTO.getTomorrowPlan());
        studyRecord.setAiSummary(updateDTO.getAiSummary());
        studyRecord.setAiComfort(updateDTO.getAiComfort());
        studyRecord.setRemark(updateDTO.getRemark());
        studyRecord.setUpdateTime(now);
    }

    private List<String> loadCategories(Long studyRecordId, Long currentUserId) {
        return studyRecordCategoryMapper.selectCategoryNamesByRecordId(studyRecordId, currentUserId);
    }

    private List<String> loadWeakPoints(Long studyRecordId, Long currentUserId) {
        return weakPointMapper.selectContentsByRecordId(studyRecordId, currentUserId);
    }

    private Set<String> normalize(List<String> values) {
        Set<String> normalized = new LinkedHashSet<>();
        if (values == null) {
            return normalized;
        }
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            normalized.add(value.trim());
        }
        return normalized;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private StudyRecordVO toListVO(StudyRecord studyRecord, List<String> categories) {
        StudyRecordVO recordVO = new StudyRecordVO();
        recordVO.setId(studyRecord.getId());
        recordVO.setRecordDate(studyRecord.getRecordDate());
        recordVO.setDurationMinutes(studyRecord.getDurationMinutes());
        recordVO.setStudyContent(studyRecord.getStudyContent());
        recordVO.setCategories(categories);
        recordVO.setEmotionStatus(studyRecord.getEmotionStatus());
        recordVO.setAiSummary(studyRecord.getAiSummary());
        recordVO.setCreateTime(studyRecord.getCreateTime());
        return recordVO;
    }

    private StudyRecordDetailVO toDetailVO(StudyRecord studyRecord, List<String> categories, List<String> weakPoints) {
        StudyRecordDetailVO detailVO = new StudyRecordDetailVO();
        detailVO.setId(studyRecord.getId());
        detailVO.setRecordDate(studyRecord.getRecordDate());
        detailVO.setDurationMinutes(studyRecord.getDurationMinutes());
        detailVO.setStudyContent(studyRecord.getStudyContent());
        detailVO.setCategories(categories);
        detailVO.setEmotionStatus(studyRecord.getEmotionStatus());
        detailVO.setAiSummary(studyRecord.getAiSummary());
        detailVO.setCreateTime(studyRecord.getCreateTime());
        detailVO.setRawContent(studyRecord.getRawContent());
        detailVO.setWeakPoints(weakPoints);
        detailVO.setTomorrowPlan(studyRecord.getTomorrowPlan());
        detailVO.setAiComfort(studyRecord.getAiComfort());
        detailVO.setRemark(studyRecord.getRemark());
        detailVO.setUpdateTime(studyRecord.getUpdateTime());
        return detailVO;
    }
}
