package com.example.accountz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.AccountDto;
import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.AccountRepository;
import com.example.accountz.persist.repository.UserRepository;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.type.AccountStatus;
import com.example.accountz.type.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtTokenExtract jwtTokenExtract;

  @InjectMocks
  private AccountService accountService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createAccount_Success() {
    // Given
    Long userId = 1L;
    UserEntity user = UserEntity.builder()
        .id(userId)
        .build();
    AccountEntity savedAccountEntity = AccountEntity.builder()
        .id(1L)
        .user(user)
        .accountStatus(AccountStatus.ACTIVATED)
        .balance(0L)
        .registeredAt(LocalDateTime.now())
        .build();

    when(jwtTokenExtract.currentUser()).thenReturn(user);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.countByUser(user)).thenReturn(0);
    when(accountRepository.save(any(AccountEntity.class))).thenAnswer(
        invocation -> {
          AccountEntity accountEntity = invocation.getArgument(0);
          accountEntity.setId(1L);
          return accountEntity;
        });

    // When
    AccountDto createdAccount = accountService.createAccount();

    // Then
    assertEquals(AccountStatus.ACTIVATED,
        savedAccountEntity.getAccountStatus());
    assertEquals(0L, createdAccount.getBalance());
    assertEquals(user.getId(), createdAccount.getUserId());
    assertEquals(1, createdAccount.getUserId());
    verify(accountRepository, times(1)).save(any(AccountEntity.class));
  }

  @Test
  void createAccount_UserNotFound_ThrowsGlobalException() {
    // Given
    Long userId = 1L;
    when(jwtTokenExtract.currentUser()).thenReturn(
        UserEntity.builder().id(userId).build());
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When
    GlobalException exception = assertThrows(GlobalException.class,
        () -> accountService.createAccount());

    // Then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    verify(accountRepository, never()).save(any(AccountEntity.class));
  }

  @Test
  void deleteAccount_Success() {
    // Given
    Long userId = 1L;
    String accountNumber = "1000000001";
    UserEntity accountUser = UserEntity.builder().id(userId).build();
    AccountEntity accountEntity = AccountEntity.builder()
        .id(1L)
        .user(accountUser)
        .accountNumber(accountNumber)
        .balance(0L)
        .accountStatus(AccountStatus.ACTIVATED)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(accountUser));
    when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accountEntity));

    // When
    AccountDto deletedAccount = accountService.deleteAccount(userId, accountNumber);

    // Then
    assertEquals(accountEntity.getId(), deletedAccount.getUserId());
    assertEquals(accountEntity.getAccountNumber(), deletedAccount.getAccountNumber());
    assertEquals(AccountStatus.UNREGISTERED, accountEntity.getAccountStatus());
    verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
  }

  @Test
  void deleteAccount_UserNotFound_ThrowsGlobalException() {
    // Given
    Long userId = 1L;
    String accountNumber = "1000000001";
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // When
    GlobalException exception = assertThrows(GlobalException.class, () -> accountService.deleteAccount(userId, accountNumber));

    // Then
    assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    verify(accountRepository, never()).save(any(AccountEntity.class));
  }

  @Test
  void deleteAccount_AccountNotFound_ThrowsGlobalException() {
    // Given
    Long userId = 1L;
    String accountNumber = "1000000001";
    when(userRepository.findById(userId)).thenReturn(Optional.of(UserEntity.builder().id(userId).build()));
    when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

    // When
    GlobalException exception = assertThrows(GlobalException.class, () -> accountService.deleteAccount(userId, accountNumber));

    // Then
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    verify(accountRepository, never()).save(any(AccountEntity.class));
  }

}