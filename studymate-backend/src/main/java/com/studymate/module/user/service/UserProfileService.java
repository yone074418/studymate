package com.studymate.module.user.service;

import com.studymate.module.user.dto.UserProfileUpdateDTO;
import com.studymate.module.user.vo.UserInfoVO;

public interface UserProfileService {

    UserInfoVO getCurrentUserProfile();

    UserInfoVO updateCurrentUserProfile(UserProfileUpdateDTO updateDTO);
}
