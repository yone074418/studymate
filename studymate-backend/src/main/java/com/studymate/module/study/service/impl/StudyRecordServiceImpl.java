package com.studymate.module.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studymate.common.exception.BusinessException;
import com.studymate.enums.ResultCode;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.entity.StudyRecord;
import com.studymate.module.study.entity.StudyRecordCategory;
import com.studymate.module.study.entity.WeakPoint;
import com.studymate.module.study.mapper.StudyRecordCategoryMapper;
import com.studymate.module.study.mapper.StudyRecordMapper;
import com.studymate.module.study.mapper.WeakPointMapper;
import com.studymate.module.study.service.StudyRecordService;
import com.studymate.module.study.vo.StudyRecordDetailVO;
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

    private void rejectDuplicate(Long currentUserId, StudyRecordCreateDTO createDTO) {
        StudyRecord existing = studyRecordMapper.selectOne(new LambdaQueryWrapper<StudyRecord>()
                .eq(StudyRecord::getUserId, currentUserId)
                .eq(StudyRecord::getRecordDate, createDTO.getRecordDate())
                .eq(StudyRecord::getRawContent, createDTO.getRawContent())
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
