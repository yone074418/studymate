package com.studymate.module.ai.service;

import com.studymate.module.ai.dto.AiAnalyzeRequestDTO;
import com.studymate.module.ai.vo.AiAnalyzeResultVO;

public interface AiRecordAnalyzeService {

    AiAnalyzeResultVO analyze(Long currentUserId, AiAnalyzeRequestDTO requestDTO);
}
