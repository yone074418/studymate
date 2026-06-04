package com.studymate.module.user.service;

import com.studymate.module.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {

    @Test
    void userServiceImplImplementsUserServiceContract() {
        UserService userService = new UserServiceImpl();

        assertThat(userService).isInstanceOf(UserService.class);
    }
}
