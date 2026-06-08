package com.studymate.module.study.service;

import com.studymate.module.study.dto.StudyRecordCreateDTO;
import com.studymate.module.study.vo.StudyRecordDetailVO;

public interface StudyRecordService {

    StudyRecordDetailVO createStudyRecord(StudyRecordCreateDTO createDTO);
}
