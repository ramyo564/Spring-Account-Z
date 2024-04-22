package com.example.accountz.webController;

import com.example.accountz.model.CreateAccountDto;
import com.example.accountz.model.DeleteAccountDto;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;
  private final JwtTokenExtract jwtTokenExtract;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/user")
  public CreateAccountDto.Response createAccount() {

    return CreateAccountDto.Response.from(accountService.createAccount());
  }

  @PreAuthorize("hasRole('USER')")
  @DeleteMapping("/user")
  public DeleteAccountDto.Response deleteAccount(
      @RequestBody @Valid DeleteAccountDto.Request request) {

    return DeleteAccountDto.Response.from(accountService.deleteAccount(
        jwtTokenExtract.currentUser().getId(),
        request.getAccountNumber()));
  }
}
