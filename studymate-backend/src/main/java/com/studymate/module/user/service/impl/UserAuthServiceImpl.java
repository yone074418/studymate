package com.studymate.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.studymate.common.exception.BusinessException;
import com.studymate.enums.ResultCode;
import com.studymate.module.user.dto.LoginDTO;
import com.studymate.module.user.dto.RegisterDTO;
import com.studymate.module.user.entity.User;
import com.studymate.module.user.mapper.UserMapper;
import com.studymate.module.user.service.UserAuthService;
import com.studymate.module.user.vo.LoginVO;
import com.studymate.module.user.vo.UserInfoVO;
import com.studymate.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private static final int DEFAULT_DAILY_TARGET_MINUTES = 120;
    private static final int ENABLED_STATUS = 1;
    private static final int NOT_DELETED = 0;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserAuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UserInfoVO register(RegisterDTO registerDTO) {
        if (existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户名已存在");
        }
        if (existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setDailyTargetMinutes(DEFAULT_DAILY_TARGET_MINUTES);
        user.setStatus(ENABLED_STATUS);
        user.setDeleted(NOT_DELETED);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insert(user);
        return toUserInfoVO(user);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        User user = findByUsernameOrEmail(loginDTO.getUsernameOrEmail());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginVO(token, toUserInfoVO(user));
    }

    private boolean existsByUsername(String username) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        return count != null && count > 0;
    }

    private boolean existsByEmail(String email) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        return count != null && count > 0;
    }

    private User findByUsernameOrEmail(String usernameOrEmail) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, usernameOrEmail)
                .or()
                .eq(User::getEmail, usernameOrEmail)
                .last("LIMIT 1"));
    }

    private UserInfoVO toUserInfoVO(User user) {
        return new UserInfoVO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getTargetPosition(),
                user.getDailyTargetMinutes(),
                user.getStudyStage()
        );
    }
}
