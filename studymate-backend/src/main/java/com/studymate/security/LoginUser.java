package com.studymate.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUser {

    private Long userId;

    private String username;
}
