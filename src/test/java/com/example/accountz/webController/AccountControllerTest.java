package com.example.accountz.webController;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.accountz.model.AccountDto;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.AccountService;
import com.example.accountz.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@WebMvcTest(AccountController.class)
class AccountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountService accountService;

  @MockBean
  private UserService userService;

  @MockBean
  private TokenProvider tokenProvider;

  @MockBean
  private JwtTokenExtract jwtTokenExtract;

  private UserEntity user;
  private String accessToken;

  @BeforeEach
  void setUp() {
    // 회원가입 및 토큰 생성
    user = UserEntity.builder()
        .id(1L)
        .email("test@example.com")
        .password("password")
        .roles(List.of("ROLE_USER"))
        .build();
    given(tokenProvider.generateToken(user.getEmail()))
        .willReturn("access_token");
    accessToken = "Bearer access_token";

  }

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void createAccountTest() throws Exception {
    // given
    AccountDto accountDto = AccountDto.builder()
        .userId(1L)
        .accountNumber("1000000001")
        .balance(0L)
        .registeredAt(LocalDateTime.now())
        .build();

    given(accountService.createAccount()).willReturn(accountDto);

    UserDetails userDetails = user;
    given(userService.loadUserByUsername(anyString())).willReturn(userDetails);

    given(tokenProvider.getAuthentication(anyString())).willReturn(
        new UsernamePasswordAuthenticationToken(
            userDetails,"",userDetails.getAuthorities()));

    // when
    ResultActions resultActions = mockMvc.perform(post("/account/user")
            .with(csrf())
        .header("Authorization", accessToken)
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId")
            .value(accountDto.getUserId()))
        .andExpect(jsonPath("$.accountNumber")
            .value(accountDto.getAccountNumber()))
        .andExpect(jsonPath("$.registeredAt")
            .isNotEmpty());

    verify(accountService, times(1))
        .createAccount();
  }
}