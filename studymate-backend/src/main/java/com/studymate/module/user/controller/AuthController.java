package com.studymate.module.user.controller;

import com.studymate.common.Result;
import com.studymate.module.user.dto.LoginDTO;
import com.studymate.module.user.dto.RegisterDTO;
import com.studymate.module.user.service.UserAuthService;
import com.studymate.module.user.vo.LoginVO;
import com.studymate.module.user.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户认证", description = "用户注册、登录和 Token 签发接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Operation(summary = "用户注册", description = "创建新用户账号。用户名和邮箱不能重复，密码使用 BCrypt 加密保存，响应不包含 password 字段。")
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return Result.success(userAuthService.register(registerDTO));
    }

    @Operation(summary = "用户登录", description = "支持用户名或邮箱登录。登录成功后返回 JWT Token 和用户基础信息。")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userAuthService.login(loginDTO));
    }
}
