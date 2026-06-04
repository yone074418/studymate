package com.studymate.module.user.service;

import com.studymate.common.exception.BusinessException;
import com.studymate.module.user.dto.LoginDTO;
import com.studymate.module.user.dto.RegisterDTO;
import com.studymate.module.user.entity.User;
import com.studymate.module.user.mapper.UserMapper;
import com.studymate.module.user.service.impl.UserAuthServiceImpl;
import com.studymate.module.user.vo.LoginVO;
import com.studymate.module.user.vo.UserInfoVO;
import com.studymate.security.JwtProperties;
import com.studymate.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

class UserAuthServiceTest {

    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        passwordEncoder = new BCryptPasswordEncoder();
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-that-is-long-enough-for-hmac-signing");
        jwtProperties.setExpirationSeconds(3600L);
        userAuthService = new UserAuthServiceImpl(userMapper, passwordEncoder, new JwtUtil(jwtProperties));
    }

    @Test
    void registerCreatesUserWithEncryptedPasswordAndReturnsBasicInfo() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(7L);
            return 1;
        });
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("alice");
        registerDTO.setEmail("alice@example.com");
        registerDTO.setPassword("plain123");

        UserInfoVO result = userAuthService.register(registerDTO);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper, times(1)).insert(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo("plain123");
        assertThat(passwordEncoder.matches("plain123", userCaptor.getValue().getPassword())).isTrue();
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(userMapper.selectCount(any())).thenReturn(1L);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("alice");
        registerDTO.setEmail("alice@example.com");
        registerDTO.setPassword("plain123");

        assertThatThrownBy(() -> userAuthService.register(registerDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名已存在");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userMapper.selectCount(any())).thenReturn(0L, 1L);
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("alice");
        registerDTO.setEmail("alice@example.com");
        registerDTO.setPassword("plain123");

        assertThatThrownBy(() -> userAuthService.register(registerDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("邮箱已存在");
    }

    @Test
    void loginReturnsTokenAndUserInfoWhenPasswordMatches() {
        User user = new User();
        user.setId(9L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword(passwordEncoder.encode("plain123"));
        when(userMapper.selectOne(any())).thenReturn(user);
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("alice");
        loginDTO.setPassword("plain123");

        LoginVO result = userAuthService.login(loginDTO);

        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getUserInfo().getUsername()).isEqualTo("alice");
        String payloadJson = new String(Base64.getUrlDecoder().decode(result.getToken().split("\\.")[1]), StandardCharsets.UTF_8);
        assertThat(payloadJson).contains("\"userId\":9");
        assertThat(payloadJson).contains("\"username\":\"alice\"");
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = new User();
        user.setId(9L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword(passwordEncoder.encode("plain123"));
        when(userMapper.selectOne(any())).thenReturn(user);
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("alice");
        loginDTO.setPassword("bad-password");

        assertThatThrownBy(() -> userAuthService.login(loginDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("用户名或密码错误");
    }
}
