package com.example.accountz.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.accountz.exception.GlobalException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

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
  void testSaveMoney_UserNotFound() {
    // given
    Long userId = 1L;
    String accountNumber = "1234567890";
    Long amount = 1000L;

    Mockito.when(userRepository.findById(userId))
        .thenReturn(Optional.empty());

    // when, then
    assertThrows(GlobalException.class, () -> {
      transactionService.saveMoney(userId, accountNumber, amount);
    });
  }

  @Test
  void testSaveMoney_AccountNotFound() {
    // given
    Long userId = 1L;
    String accountNumber = "1234567890";
    Long amount = 1000L;
    UserEntity userEntity = new UserEntity();
    userEntity.setId(userId);

    Mockito.when(userRepository.findById(userId))
        .thenReturn(Optional.of(userEntity));
    Mockito.when(accountRepository.findByAccountNumber(accountNumber))
        .thenReturn(Optional.empty());

    // when, then
    assertThrows(GlobalException.class, () -> {
      transactionService.saveMoney(userId, accountNumber, amount);
    });
  }


  @Test
  void useBalance_Success() {
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
  void overMillionSendMoney_Success() {
    // Given
    Long userId = 1L;
    String senderAccountNumber = "1234567890";
    String userName = "John Doe";
    LocalDate userBirthDay = LocalDate.of(1990, 1, 1);
    String userEmail = "john.doe@example.com";
    String userPassword = "password";
    String receiverAccountNumber = "0987654321";
    Long amount = 10000000L;

    UserEntity user = UserEntity.builder()
        .id(userId)
        .name(userName)
        .birthDay(userBirthDay)
        .email(userEmail)
        .password(passwordEncoder.encode(userPassword))
        .build();
    AccountEntity senderAccount = AccountEntity.builder()
        .accountNumber(senderAccountNumber)
        .accountStatus(AccountStatus.ACTIVATED)
        .user(user)
        .balance(amount * 2)
        .build();
    AccountEntity receiverAccount = AccountEntity.builder()
        .accountNumber(receiverAccountNumber)
        .accountStatus(AccountStatus.ACTIVATED)
        .build();
    TransactionEntity transaction = TransactionEntity.builder()
        .transactionType(TransactionType.USE)
        .transactionResultType(TransactionResultType.SUCCESS)
        .account(senderAccount)
        .amount(amount)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.findByAccountNumber(
        senderAccountNumber)).thenReturn(Optional.of(senderAccount));
    when(accountRepository.findByAccountNumber(
        receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
    when(passwordEncoder.matches(userPassword,
        user.getPassword())).thenReturn(true);
    when(transactionRepository.save(
        any(TransactionEntity.class))).thenReturn(transaction);

    // When
    TransactionDto result = transactionService.overMillionSendMoney(userId,
        senderAccountNumber, userName, userBirthDay, userEmail,
        userPassword, receiverAccountNumber, 1000_000L);

    // Then
    verify(userRepository, times(1)).findById(userId);
    verify(accountRepository, times(1)).findByAccountNumber(
        senderAccountNumber);
    verify(accountRepository, times(1)).findByAccountNumber(
        receiverAccountNumber);
    verify(passwordEncoder, times(1)).matches(userPassword,
        user.getPassword());
    verify(transactionRepository, times(1)).save(
        any(TransactionEntity.class));
    assertEquals(transaction.getTransactionType(),
        result.getTransactionType());
    assertEquals(transaction.getTransactionResultType(),
        result.getTransactionResultType());
    assertEquals(transaction.getAmount(), result.getAmount());
  }

  @Test
  void overMillionSendMoney_Fail_UserUnmatched() {
    // Given
    Long userId = 1L;
    String senderAccountNumber = "1234567890";
    String userName = "John Doe";
    LocalDate userBirthDay = LocalDate.of(1990, 1, 1);
    String userEmail = "john.doe@example.com";
    String userPassword = "password";
    String receiverAccountNumber = "0987654321";
    Long amount = 1000000L;

    UserEntity user = UserEntity.builder()
        .id(userId)
        .name("Wrong Name") // 잘못된 이름
        .birthDay(userBirthDay)
        .email(userEmail)
        .password(passwordEncoder.encode(userPassword))
        .build();
    AccountEntity senderAccount = AccountEntity.builder()
        .accountNumber(senderAccountNumber)
        .accountStatus(AccountStatus.ACTIVATED)
        .user(user)
        .balance(amount * 2)
        .build();
    AccountEntity receiverAccount = AccountEntity.builder()
        .accountNumber(receiverAccountNumber)
        .accountStatus(AccountStatus.ACTIVATED)
        .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(accountRepository.findByAccountNumber(
        senderAccountNumber)).thenReturn(Optional.of(senderAccount));
    when(accountRepository.findByAccountNumber(
        receiverAccountNumber)).thenReturn(Optional.of(receiverAccount));
    when(passwordEncoder.matches(userPassword,
        user.getPassword())).thenReturn(true);

    // When & Then
    assertThrows(GlobalException.class,
        () -> transactionService.overMillionSendMoney(userId,
            senderAccountNumber, userName, userBirthDay, userEmail,
            userPassword, receiverAccountNumber, amount));
  }

  @Test
  void overMillionSendMoney_Fail_UserNotFound() {
    // given
    Long userId = 1L;
    String senderAccountNumber = "1234567890";
    String userName = "John";
    LocalDate userBirthDay = LocalDate.of(1990, 1, 1);
    String userEmail = "john@example.com";
    String userPassword = "password";
    String receiverAccountNumber = "0987654321";
    Long amount = 1000L;

    Mockito.when(userRepository.findById(userId))
        .thenReturn(Optional.empty());

    // when, then
    assertThrows(GlobalException.class, () -> {
      transactionService.overMillionSendMoney(userId, senderAccountNumber,
          userName, userBirthDay, userEmail, userPassword,
          receiverAccountNumber, amount);
    });
  }

  @Test
  void overMillionSendMoney_Fail_SenderAccountNotFound() {
    // given
    Long userId = 1L;
    String senderAccountNumber = "1234567890";
    String userName = "John";
    LocalDate userBirthDay = LocalDate.of(1990, 1, 1);
    String userEmail = "john@example.com";
    String userPassword = "password";
    String receiverAccountNumber = "0987654321";
    Long amount = 1000L;
    UserEntity userEntity = new UserEntity();
    userEntity.setId(userId);
    userEntity.setName(userName);
    userEntity.setBirthDay(userBirthDay);
    userEntity.setEmail(userEmail);
    userEntity.setPassword(passwordEncoder.encode(userPassword));

    Mockito.when(userRepository.findById(userId))
        .thenReturn(Optional.of(userEntity));
    Mockito.when(
            accountRepository.findByAccountNumber(senderAccountNumber))
        .thenReturn(Optional.empty());

    // when, then
    assertThrows(GlobalException.class, () -> {
      transactionService.overMillionSendMoney(userId, senderAccountNumber,
          userName, userBirthDay, userEmail, userPassword,
          receiverAccountNumber, amount);
    });
  }


}