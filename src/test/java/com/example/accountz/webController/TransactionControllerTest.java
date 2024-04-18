package com.example.accountz.webController;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.TransactionDto;
import com.example.accountz.model.UseBalanceDto;
import com.example.accountz.model.UseBalanceDto.Response;
import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.TransactionEntity;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.TransactionRepository;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.TransactionService;
import com.example.accountz.service.UserService;
import com.example.accountz.type.AccountStatus;
import com.example.accountz.type.ErrorCode;
import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @MockBean
  private TransactionService transactionService;

  @MockBean
  private JwtTokenExtract jwtTokenExtract;

  @MockBean
  private TokenProvider tokenProvider;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private TransactionRepository transactionRepository;

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
  void userSaveMoney() throws Exception {
    //given
    Long userId = user.getId();
    String accountNumber = "1000000001";
    Long amount = 1000L;
    LocalDateTime localDateTime = LocalDateTime.of(
        12, 12, 12, 12, 12, 12);
    given(transactionService.saveMoney(
        anyLong(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .accountNumber(accountNumber)
            .transactionType(TransactionType.USE)
            .transactionResultType(
                TransactionResultType.SUCCESS)
            .amount(amount)
            .transactionId("transactionIdForCancel")
            .balanceSnapshot(5000L)
            .transactedAt(localDateTime)
            .build());

    // When
    UseBalanceDto.Request request = new UseBalanceDto.Request(
        accountNumber, amount);
    ResultActions resultActions = mockMvc.perform(
        post("/transaction/save-money")
            .with(csrf())
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

    // Then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.accountNumber").value(accountNumber))
        .andExpect(jsonPath("$.transactionResultType").value(
            TransactionResultType.SUCCESS.toString()))
        .andExpect(jsonPath("$.transactionId").exists())
        .andExpect(jsonPath("$.transactionAt")
            .value(localDateTime.toString()));

    verify(transactionService, times(1))
        .saveMoney(userId, accountNumber,
            amount);
  }

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void useBalance() throws Exception {
    //given
    Long userId = user.getId();
    String accountNumber = "1000000001";
    Long amount = 1000L;
    LocalDateTime localDateTime = LocalDateTime.of(
        12, 12, 12, 12, 12, 12);
    given(transactionService.useBalance(
        anyLong(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .accountNumber(accountNumber)
            .transactionType(TransactionType.USE)
            .transactionResultType(
                TransactionResultType.SUCCESS)
            .amount(amount)
            .transactionId("transactionIdForCancel")
            .balanceSnapshot(500L)
            .transactedAt(localDateTime)
            .build());

    // When
    UseBalanceDto.Request request = new UseBalanceDto.Request(
        accountNumber, amount);
    ResultActions resultActions = mockMvc.perform(
        post("/transaction/withdraw")
            .with(csrf())
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

    // Then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.accountNumber").value(accountNumber))
        .andExpect(jsonPath("$.transactionResultType").value(
            TransactionResultType.SUCCESS.toString()))
        .andExpect(jsonPath("$.transactionId").exists())
        .andExpect(jsonPath("$.transactionAt")
            .value(localDateTime.toString()));

    verify(transactionService, times(1))
        .useBalance(userId, accountNumber,
            amount);
  }
}
