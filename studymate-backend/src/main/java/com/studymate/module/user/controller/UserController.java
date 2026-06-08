package com.studymate.module.user.controller;

import com.studymate.common.Result;
import com.studymate.module.user.dto.UserProfileUpdateDTO;
import com.studymate.module.user.service.UserProfileService;
import com.studymate.module.user.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户信息", description = "当前登录用户个人信息接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Operation(
            summary = "获取当前登录用户个人信息",
            description = "需要登录。根据 Authorization Bearer Token 解析当前用户 ID，返回用户基础信息，不包含 password 字段。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/profile")
    public Result<UserInfoVO> getProfile() {
        return Result.success(userProfileService.getCurrentUserProfile());
    }

    @Operation(
            summary = "修改当前登录用户个人信息",
            description = "需要登录。仅允许修改 nickname、targetPosition、dailyTargetMinutes、studyStage、avatarUrl，不能修改 username、email、password。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(@Valid @RequestBody UserProfileUpdateDTO updateDTO) {
        return Result.success(userProfileService.updateCurrentUserProfile(updateDTO));
    }
}
