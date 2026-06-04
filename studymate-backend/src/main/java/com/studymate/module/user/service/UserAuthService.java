package com.studymate.module.user.service;

import com.studymate.module.user.dto.LoginDTO;
import com.studymate.module.user.dto.RegisterDTO;
import com.studymate.module.user.vo.LoginVO;
import com.studymate.module.user.vo.UserInfoVO;

public interface UserAuthService {

    UserInfoVO register(RegisterDTO registerDTO);

    LoginVO login(LoginDTO loginDTO);
}
