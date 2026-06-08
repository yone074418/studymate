package com.studymate.module.study.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.dto.StudyRecordQueryDTO;
import com.studymate.module.study.dto.StudyRecordUpdateDTO;
import com.studymate.module.study.vo.StudyRecordDetailVO;
import com.studymate.module.study.vo.StudyRecordVO;

public interface StudyRecordService {

    StudyRecordDetailVO createStudyRecord(StudyRecordCreateDTO createDTO);

    IPage<StudyRecordVO> listStudyRecords(StudyRecordQueryDTO queryDTO);

    StudyRecordDetailVO getStudyRecordDetail(Long id);

    StudyRecordDetailVO updateStudyRecord(Long id, StudyRecordUpdateDTO updateDTO);

    void deleteStudyRecord(Long id);
}
