package com.example.accountz.security;

import com.example.accountz.persist.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenExtract {

  public UserEntity currentUser() {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    return (UserEntity) authentication.getPrincipal();
  }

}

