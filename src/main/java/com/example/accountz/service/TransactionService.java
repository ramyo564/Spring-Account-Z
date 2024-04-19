package com.example.accountz.service;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.TransactionDto;
import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.TransactionEntity;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.AccountRepository;
import com.example.accountz.persist.repository.TransactionRepository;
import com.example.accountz.persist.repository.UserRepository;
import com.example.accountz.type.AccountStatus;
import com.example.accountz.type.ErrorCode;
import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final TransactionRepository transactionRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public TransactionDto saveMoney(
      Long userId, String accountNumber, Long amount) {
    UserEntity user = userRepository.findById(userId).orElseThrow(() ->
        new GlobalException(ErrorCode.USER_NOT_FOUND));
    AccountEntity account = accountRepository
        .findByAccountNumber(accountNumber).orElseThrow(() ->
            new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    saveValidateMoney(user, account);

    account.saveMoney(amount);

    return TransactionDto.fromEntity(
        saveTransaction(
            TransactionType.USE,
            TransactionResultType.SUCCESS,
            account,
            amount,
            user));
  }

  private static void saveValidateMoney(
      UserEntity user, AccountEntity account) {
    if (!Objects.equals(
        user.getId(), account.getUser().getId())) {
      throw new GlobalException(ErrorCode.USER_ACCOUNT_UNMATCHED);
    }
    if (account.getAccountStatus() != AccountStatus.ACTIVATED) {
      throw new GlobalException(
          ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
    }
  }

  @Transactional
  public TransactionDto useBalance(
      Long userId, String accountNumber, Long amount) {

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    AccountEntity account = accountRepository.findByAccountNumber(
            accountNumber)
        .orElseThrow(
            () -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    validateUseBalance(user, account, amount);

    account.useBalance(amount);

    return TransactionDto.fromEntity(
        saveTransaction(
            TransactionType.USE,
            TransactionResultType.SUCCESS,
            account,
            amount,
            user));
  }

  private void validateUseBalance(
      UserEntity user, AccountEntity account, Long amount) {
    if (!Objects.equals(
        user.getId(), account.getUser().getId())) {
      throw new GlobalException(ErrorCode.USER_ACCOUNT_UNMATCHED);
    }
    if (account.getAccountStatus() != AccountStatus.ACTIVATED) {
      throw new GlobalException(
          ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
    }
    if (account.getBalance() < amount) {
      throw new GlobalException(ErrorCode.AMOUNT_EXCEED_BALANCE);
    }
  }

  private TransactionEntity saveTransaction(
      TransactionType transactionType,
      TransactionResultType transactionResultType,
      AccountEntity account,
      Long amount,
      UserEntity user
  ) {
    return transactionRepository.save(
        TransactionEntity.builder()
            .user(user)
            .transactionType(transactionType)
            .transactionResultType(transactionResultType)
            .account(account)
            .amount(amount)
            .balanceSnapshot(account.getBalance())
            .transactionId(
                UUID.randomUUID()
                    .toString()
                    .replace("-", ""))
            .transactedAt(LocalDateTime.now())
            .build());
  }

  @Transactional
  public void saveFailedUseTransaction(
      UserEntity user, String accountNumber, Long amount) {
    AccountEntity account = accountRepository.findByAccountNumber(
        accountNumber).orElseThrow(() ->
        new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    saveTransaction(
        TransactionType.USE,
        TransactionResultType.FAIL,
        account,
        amount,
        user
    );
  }

  @Transactional
  public TransactionDto overMillionSendMoney(
      Long userId,
      String senderAccountNumber,
      String userName,
      LocalDate userBirthDay,
      String userEmail,
      String userPassword,
      String receiverAccountNumber,
      Long amount
  ) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    AccountEntity userAccount = accountRepository.findByAccountNumber(
        senderAccountNumber).orElseThrow(
        () -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    AccountEntity receiverAccount = accountRepository.findByAccountNumber(
        receiverAccountNumber).orElseThrow(
        () -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    validateUserInfo(userName, userBirthDay, userEmail, userPassword,
        user);
    validateReceiver(receiverAccount);

    userAccount.useBalance(amount);
    receiverAccount.saveMoney(amount);

    return TransactionDto.fromEntity(
        saveTransaction(
            TransactionType.USE,
            TransactionResultType.SUCCESS,
            userAccount,
            amount,
            user));
  }

  private void validateUserInfo(String userName, LocalDate userBirthDay,
      String userEmail, String userPassword, UserEntity user) {
    boolean matches = this.passwordEncoder.matches(userPassword,
        user.getPassword());

    if (!user.getName().equals(userName)) {
      throw new GlobalException(ErrorCode.UNMATCHED_USER);
    }
    if (!user.getBirthDay().isEqual(userBirthDay)) {
      throw new GlobalException(ErrorCode.UNMATCHED_BIRTHDAY);
    }
    if (!user.getEmail().equals(userEmail)) {
      throw new GlobalException(ErrorCode.UNMATCHED_EMAIL);
    }
    if (!matches) {
      throw new GlobalException(ErrorCode.UNMATCHED_PASSWORD);
    }
  }

  private static void validateReceiver(AccountEntity otherAccount) {
    if (otherAccount.getAccountStatus() != AccountStatus.ACTIVATED) {
      throw new GlobalException(
          ErrorCode.RECEIVER_ACCOUNT_UNREGISTERED);
    }
  }

  @Transactional
  public TransactionDto underMillionSendMoney(
      Long userId,
      String senderAccountNumber,
      String receiverAccountNumber,
      Long amount
  ) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    AccountEntity userAccount = accountRepository.findByAccountNumber(
        senderAccountNumber).orElseThrow(
        () -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    AccountEntity receiverAccount = accountRepository.findByAccountNumber(
        receiverAccountNumber).orElseThrow(
        () -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    validateReceiver(receiverAccount);
    userAccount.useBalance(amount);
    receiverAccount.saveMoney(amount);

    return TransactionDto.fromEntity(
        saveTransaction(
            TransactionType.USE,
            TransactionResultType.SUCCESS,
            userAccount,
            amount,
            user));
  }
}
