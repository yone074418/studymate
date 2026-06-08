package com.studymate.module.user.service;

import com.studymate.module.user.dto.UserProfileUpdateDTO;
import com.studymate.module.user.entity.User;
import com.studymate.module.user.mapper.UserMapper;
import com.studymate.module.user.service.impl.UserProfileServiceImpl;
import com.studymate.module.user.vo.UserInfoVO;
import com.studymate.security.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    private UserMapper userMapper;
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        userProfileService = new UserProfileServiceImpl(userMapper);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new LoginUser(9L, "alice"),
                null,
                Collections.emptyList()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserProfileUsesCurrentUserIdAndDoesNotExposePassword() {
        User user = createUser();
        when(userMapper.selectById(eq(9L))).thenReturn(user);

        UserInfoVO result = userProfileService.getCurrentUserProfile();

        assertThat(result.getId()).isEqualTo(9L);
        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("avatarUrl");
    }

    @Test
    void updateCurrentUserProfileUsesCurrentUserIdAndPreservesOmittedFields() {
        User user = createUser();
        when(userMapper.selectById(eq(9L))).thenReturn(user);
        UserProfileUpdateDTO updateDTO = new UserProfileUpdateDTO();
        updateDTO.setNickname("New Alice");
        updateDTO.setTargetPosition("Java Backend Intern");
        updateDTO.setDailyTargetMinutes(150);

        UserInfoVO result = userProfileService.updateCurrentUserProfile(updateDTO);

        assertThat(result.getNickname()).isEqualTo("New Alice");
        assertThat(result.getTargetPosition()).isEqualTo("Java Backend Intern");
        assertThat(result.getDailyTargetMinutes()).isEqualTo(150);
        assertThat(result.getStudyStage()).isEqualTo("基础复习");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(userCaptor.capture());
        assertThat(userCaptor.getValue().getId()).isEqualTo(9L);
        assertThat(userCaptor.getValue().getUsername()).isEqualTo("alice");
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("alice@example.com");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encrypted-password");
    }

    private User createUser() {
        User user = new User();
        user.setId(9L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("encrypted-password");
        user.setNickname("Alice");
        user.setTargetPosition("Java Intern");
        user.setDailyTargetMinutes(120);
        user.setStudyStage("基础复习");
        return user;
    }
}
