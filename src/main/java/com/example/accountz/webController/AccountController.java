package com.example.accountz.webController;

import com.example.accountz.model.CreateAccountDto;
import com.example.accountz.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/user")
  public CreateAccountDto.Response createAccount() {

    return CreateAccountDto.Response.from(accountService.createAccount());
  }
}
