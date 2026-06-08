package com.studymate.module.user.service.impl;

import com.studymate.common.exception.BusinessException;
import com.studymate.enums.ResultCode;
import com.studymate.module.user.dto.UserProfileUpdateDTO;
import com.studymate.module.user.entity.User;
import com.studymate.module.user.mapper.UserMapper;
import com.studymate.module.user.service.UserProfileService;
import com.studymate.module.user.vo.UserInfoVO;
import com.studymate.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserMapper userMapper;

    public UserProfileServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserInfoVO getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toUserInfoVO(user);
    }

    @Override
    @Transactional
    public UserInfoVO updateCurrentUserProfile(UserProfileUpdateDTO updateDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getTargetPosition() != null) {
            user.setTargetPosition(updateDTO.getTargetPosition());
        }
        if (updateDTO.getDailyTargetMinutes() != null) {
            user.setDailyTargetMinutes(updateDTO.getDailyTargetMinutes());
        }
        if (updateDTO.getStudyStage() != null) {
            user.setStudyStage(updateDTO.getStudyStage());
        }
        if (updateDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(updateDTO.getAvatarUrl());
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return toUserInfoVO(user);
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
