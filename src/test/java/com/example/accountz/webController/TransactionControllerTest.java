package com.example.accountz.webController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.accountz.model.CancelBalanceDto;
import com.example.accountz.model.SendOverMillionMoneyDto;
import com.example.accountz.model.SendUnderMillionMoneyDto;
import com.example.accountz.model.TransactionDto;
import com.example.accountz.model.UseBalanceDto;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.security.TokenProvider;
import com.example.accountz.service.TransactionService;
import com.example.accountz.service.UserService;
import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

  private UserEntity user;
  private String accessToken;

  @BeforeEach
  void setUp() {
    // 회원가입 및 토큰 생성
    user = UserEntity.builder()
        .id(1L)
        .name("김김김")
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
            .userAccountNumber(accountNumber)
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
            .userAccountNumber(accountNumber)
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

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void underMillionSendMoney_Success() throws Exception {
    // Given
    Long userId = user.getId();
    String userAccountNumber = "1234567890";
    String receiverAccountNumber = "0987654321";
    Long amount = 100000L;
    LocalDateTime localDateTime = LocalDateTime.of(
        12, 12, 12, 12, 12, 12);
    given(transactionService.underMillionSendMoney(
        anyLong(), anyString(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .userAccountNumber(userAccountNumber)
            .receiverAccountNumber(receiverAccountNumber)
            .transactionType(TransactionType.USE)
            .transactionResultType(
                TransactionResultType.SUCCESS)
            .amount(amount)
            .transactionId("transactionIdForCancel")
            .balanceSnapshot(500L)
            .transactedAt(localDateTime)
            .build());
    // When & Then
    SendUnderMillionMoneyDto.Request request = new SendUnderMillionMoneyDto.Request(
        userAccountNumber, receiverAccountNumber, amount);
    mockMvc.perform(post("/transaction/under-million-send-money")
            .with(csrf())
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.userAccountNumber").value(userAccountNumber))
        .andExpect(jsonPath("$.receiverAccountNumber").value(
            receiverAccountNumber))
        .andExpect(jsonPath("$.transactionResultType").value(
            TransactionResultType.SUCCESS.toString()))
        .andExpect(jsonPath("$.amount").value(amount))
        .andExpect(
            jsonPath("$.transactionAt").value(localDateTime.toString()));
  }

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void overMillionSendMoney_Success() throws Exception {
    // Given
    Long userId = user.getId();
    String userAccountNumber = "1234567890";
    String receiverAccountNumber = "0987654321";
    Long amount = 1000000L;
    String password = "123456789";
    LocalDate birthday = LocalDate.of(1990, 1, 1);
    LocalDateTime localDateTime = LocalDateTime.of(
        12, 12, 12, 12, 12, 12);
    given(transactionService.overMillionSendMoney(
        anyLong(), anyString(), anyString(), any(), anyString(),
        anyString(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .userAccountNumber(userAccountNumber)
            .receiverAccountNumber(receiverAccountNumber)
            .transactionType(TransactionType.USE)
            .transactionResultType(
                TransactionResultType.SUCCESS)
            .amount(amount)
            .transactionId("transactionIdForCancel")
            .balanceSnapshot(10000000L)
            .transactedAt(localDateTime)
            .build());
    // When & Then
    SendOverMillionMoneyDto.Request request = new SendOverMillionMoneyDto.Request(
        userAccountNumber, user.getName(), birthday, user.getEmail(),
        password,
        receiverAccountNumber, amount);
    mockMvc.perform(post("/transaction/over-million-send-money")
            .with(csrf())
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.userAccountNumber").value(userAccountNumber))
        .andExpect(jsonPath("$.receiverAccountNumber").value(
            receiverAccountNumber))
        .andExpect(jsonPath("$.transactionResultType").value(
            TransactionResultType.SUCCESS.toString()))
        .andExpect(jsonPath("$.amount").value(amount))
        .andExpect(
            jsonPath("$.transactionAt").value(localDateTime.toString()));
  }

  @Test
  @WithMockUser(username = "test@example.com", roles = "USER")
  void cancelBalance_Success() throws Exception {
    // Given
    String userAccountNumber = "1234567890";
    String receiverAccountNumber = "0987654321";
    Long amount = 100000L;
    String transactionId = "transactionIDID";
    LocalDateTime localDateTime = LocalDateTime.of(
        12, 12, 12, 12, 12, 12);
    given(transactionService.cancelBalance(
        anyLong(), anyString(), anyString(), anyString(), anyLong()))
        .willReturn(TransactionDto.builder()
            .userAccountNumber(userAccountNumber)
            .receiverAccountNumber(receiverAccountNumber)
            .transactionType(TransactionType.CANCEL)
            .transactionResultType(
                TransactionResultType.SUCCESS)
            .amount(amount)
            .transactionId("transactionIdForCancel")
            .balanceSnapshot(500L)
            .transactedAt(localDateTime)
            .build());

    CancelBalanceDto.Request request = new CancelBalanceDto.Request(
        transactionId, userAccountNumber, receiverAccountNumber, amount);

    // When & Then
    mockMvc.perform(post("/transaction/cancel-transaction")
            .with(csrf())
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.userAccountNumber").value(userAccountNumber))
        .andExpect(jsonPath("$.receiverAccountNumber").value(
            receiverAccountNumber))
        .andExpect(jsonPath("$.transactionResultType").value(
            TransactionResultType.SUCCESS.toString()))
        .andExpect(jsonPath("$.amount").value(amount))
        .andExpect(
            jsonPath("$.transactionAt").value(localDateTime.toString()));
  }

}
