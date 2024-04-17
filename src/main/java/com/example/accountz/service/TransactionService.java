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
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final TransactionRepository transactionRepository;

  @Transactional
  public TransactionDto saveMoney(
      Long userId, String accountNumber, Long amount) {
    UserEntity user = userRepository.findById(userId).orElseThrow(() ->
        new GlobalException(ErrorCode.USER_NOT_FOUND));
    AccountEntity account = accountRepository
        .findByAccountNumber(accountNumber).orElseThrow(() ->
            new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    saveValidateMoney(amount, user, account);

    account.saveMoney(amount);

    return TransactionDto.fromEntity(
        saveTransaction(
            TransactionType.USE,
            TransactionResultType.SUCCESS,
            account,
            amount
        )
    );

  }

  private static void saveValidateMoney(Long amount, UserEntity user,
      AccountEntity account) {
    if (!Objects.equals(
        user.getId(), account.getUser().getId())) {
      throw new GlobalException(ErrorCode.USER_ACCOUNT_UNMATCHED);
    }
    if (account.getAccountStatus() != AccountStatus.ACTIVATED) {
      throw new GlobalException(
          ErrorCode.ACCOUNT_ALREADY_UNREGISTERED
      );
    }
  }

  @Transactional
  public TransactionDto useBalance(
      Long userId, String accountNumber, Long amount) {

    UserEntity user = userRepository
        .findById(userId)
        .orElseThrow(() ->
            new GlobalException(ErrorCode.USER_NOT_FOUND));
    AccountEntity account = accountRepository
        .findByAccountNumber(accountNumber)
        .orElseThrow(() ->
            new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    validateUseBalance(user, account, amount);

    account.useBalance(amount);

    return TransactionDto.fromEntity(
        saveTransaction(
            TransactionType.USE,
            TransactionResultType.SUCCESS,
            account,
            amount
        )
    );
  }

  private void validateUseBalance(
      UserEntity user, AccountEntity account, Long amount) {
    if (!Objects.equals(
        user.getId(), account.getUser().getId())) {
      throw new GlobalException(ErrorCode.USER_ACCOUNT_UNMATCHED);
    }
    if (account.getAccountStatus() != AccountStatus.ACTIVATED) {
      throw new GlobalException(
          ErrorCode.ACCOUNT_ALREADY_UNREGISTERED
      );
    }
    if (account.getBalance() < amount) {
      throw new GlobalException(ErrorCode.AMOUNT_EXCEED_BALANCE);
    }
  }

  private TransactionEntity saveTransaction(
      TransactionType transactionType,
      TransactionResultType transactionResultType,
      AccountEntity account,
      Long amount
  ) {
    return transactionRepository.save(
        TransactionEntity.builder()
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
            .build()
    );
  }

  @Transactional
  public void saveFailedUseTransaction(
      String accountNumber, Long amount) {
    AccountEntity account = accountRepository.findByAccountNumber(
            accountNumber)
        .orElseThrow(
            () -> new GlobalException(ErrorCode.ACCOUNT_NOT_FOUND));

    saveTransaction(
        TransactionType.USE,
        TransactionResultType.FAIL,
        account,
        amount
    );
  }

}
