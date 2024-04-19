package com.example.accountz.webController;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.accountz.model.AccountDto;
import com.example.accountz.model.DeleteAccountDto;
import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.AccountRepository;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.AccountService;
import com.example.accountz.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


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

  @MockBean
  private AccountRepository accountRepository;

  @Autowired
  private ObjectMapper objectMapper;

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
    UserDetails userDetails = user;
    given(userService.loadUserByUsername(anyString())).willReturn(
        userDetails);

    given(tokenProvider.getAuthentication(anyString())).willReturn(
        new UsernamePasswordAuthenticationToken(
            userDetails, "", userDetails.getAuthorities()));
    given(jwtTokenExtract.currentUser()).willReturn(user);

  }

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void createAccountTest() throws Exception {
    // given
    AccountDto accountDto = AccountDto.builder()
        .userId(user.getId())
        .accountNumber("1000000001")
        .balance(0L)
        .registeredAt(LocalDateTime.now())
        .build();

    given(accountService.createAccount()).willReturn(accountDto);

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

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void deleteAccountTest() throws Exception {
    // given
    AccountDto accountDto = AccountDto.builder()
        .userId(user.getId())
        .accountNumber("1000000001")
        .balance(0L)
        .registeredAt(LocalDateTime.now())
        .build();

    AccountDto deleteAccountDto = AccountDto.builder()
        .userId(user.getId())
        .accountNumber("1000000001")
        .balance(0L)
        .unRegisteredAt(LocalDateTime.now())
        .build();
    given(accountService.createAccount()).willReturn(accountDto);

    given(accountService.deleteAccount(anyLong(), anyString()))
        .willReturn(deleteAccountDto);

    // when
    accountService.createAccount();
    ResultActions resultActions = mockMvc.perform(delete("/account/user")
        .with(csrf())
        .header("Authorization", accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(
            new DeleteAccountDto.Request("1000000001")
        )));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(accountDto.getUserId()))
        .andExpect(jsonPath("$.accountNumber").value(
            accountDto.getAccountNumber()))
        .andExpect(jsonPath("$.unRegisteredAt").isNotEmpty());

    ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(
        AccountEntity.class);
    verify(accountService, times(1))
        .createAccount();
    verify(accountService, times(1))
        .deleteAccount(anyLong(), anyString());
  }
}