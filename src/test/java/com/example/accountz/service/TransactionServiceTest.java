package com.example.accountz.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.accountz.model.TransactionDto;
import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.TransactionEntity;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.AccountRepository;
import com.example.accountz.persist.repository.TransactionRepository;
import com.example.accountz.persist.repository.UserRepository;
import com.example.accountz.type.AccountStatus;
import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  void saveMoney_success() {
    // Given
    Long userId = 1L;
    String accountNumber = "123456789";
    Long amount = 10000L;

    UserEntity user = UserEntity.builder()
        .id(userId)
        .build();
    AccountEntity account = AccountEntity.builder()
        .id(1L)
        .balance(amount)
        .accountNumber(accountNumber)
        .user(user)
        .accountStatus(AccountStatus.ACTIVATED)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(
        Optional.of(account));
    when(transactionRepository.save(
        any(TransactionEntity.class))).thenReturn(
        TransactionEntity.builder()
            .id(1L)
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.SUCCESS)
            .account(account)
            .amount(amount)
            .balanceSnapshot(account.getBalance() + amount)
            .transactionId("1234567890")
            .transactedAt(LocalDateTime.now())
            .build()
    );

    // When
    TransactionDto transactionDto = transactionService.saveMoney(userId,
        accountNumber, amount);

    // Then
    verify(accountRepository, times(1))
        .findByAccountNumber(accountNumber);
    verify(userRepository, times(1)).findById(userId);
    verify(transactionRepository, times(1)).save(
        any(TransactionEntity.class));
    assertThat(transactionDto.getAmount()).isEqualTo(amount);
    assertThat(transactionDto.getTransactionResultType()).isEqualTo(
        TransactionResultType.SUCCESS);
  }

  @Test
  void useBalance_success() {
    // Given
    Long userId = 1L;
    String accountNumber = "123456789";
    Long amount = 10000L;

    UserEntity user = UserEntity.builder()
        .id(userId)
        .build();
    AccountEntity account = AccountEntity.builder()
        .id(1L)
        .balance(amount)
        .accountNumber(accountNumber)
        .user(user)
        .accountStatus(AccountStatus.ACTIVATED)
        .balance(20000L)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(
        Optional.of(account));
    when(transactionRepository.save(
        any(TransactionEntity.class))).thenReturn(
        TransactionEntity.builder()
            .id(1L)
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.SUCCESS)
            .account(account)
            .amount(amount)
            .balanceSnapshot(account.getBalance() - amount)
            .transactionId("1234567890")
            .transactedAt(LocalDateTime.now())
            .build()
    );

    // When
    TransactionDto transactionDto = transactionService.useBalance(userId,
        accountNumber, amount);

    // Then
    verify(accountRepository, times(1))
        .findByAccountNumber(accountNumber);
    verify(userRepository, times(1)).findById(userId);
    verify(transactionRepository, times(1)).save(
        any(TransactionEntity.class));
    assertThat(transactionDto.getAmount()).isEqualTo(amount);
    assertThat(transactionDto.getTransactionResultType()).isEqualTo(
        TransactionResultType.SUCCESS);
  }

  @Test
  void saveFailedUseTransaction_success() {
    // Given
    String accountNumber = "123456789";
    Long amount = 10000L;

    AccountEntity account = AccountEntity.builder()
        .id(1L)
        .balance(amount)
        .accountNumber(accountNumber)
        .accountStatus(AccountStatus.ACTIVATED)
        .build();

    when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(
        Optional.of(account));
    when(transactionRepository.save(
        any(TransactionEntity.class))).thenReturn(
        TransactionEntity.builder()
            .id(1L)
            .transactionType(TransactionType.USE)
            .transactionResultType(TransactionResultType.FAIL)
            .account(account)
            .amount(amount)
            .balanceSnapshot(account.getBalance())
            .transactionId("1234567890")
            .transactedAt(LocalDateTime.now())
            .build()
    );

    // When
    transactionService.saveFailedUseTransaction(accountNumber, amount);

    // Then
    verify(accountRepository, times(1))
        .findByAccountNumber(accountNumber);
    verify(transactionRepository, times(1)).save(
        any(TransactionEntity.class));
  }
}