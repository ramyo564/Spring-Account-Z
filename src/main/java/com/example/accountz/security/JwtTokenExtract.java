package com.example.accountz.security;

import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@RequiredArgsConstructor

public class JwtTokenExtract {

    private final UserService userService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public static UserEntity currentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }


}
