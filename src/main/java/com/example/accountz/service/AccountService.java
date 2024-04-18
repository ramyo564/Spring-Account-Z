package com.example.accountz.service;

import static com.example.accountz.type.ErrorCode.ACCOUNT_NOT_FOUND;
import static com.example.accountz.type.ErrorCode.USER_NOT_FOUND;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.AccountDto;
import com.example.accountz.persist.entity.AccountEntity;
import com.example.accountz.persist.entity.UserEntity;
import com.example.accountz.persist.repository.AccountRepository;
import com.example.accountz.persist.repository.UserRepository;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.type.AccountStatus;
import com.example.accountz.type.ErrorCode;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final JwtTokenExtract jwtTokenExtract;


  @Transactional
  public AccountDto createAccount() {
    Long userId = jwtTokenExtract.currentUser().getId();

    UserEntity user = userRepository.findById(userId).orElseThrow(() ->
        new GlobalException(USER_NOT_FOUND));

    checkAccountLimit_5(user);

    return AccountDto.fromEntity(accountRepository.save(
        AccountEntity.builder()
            .user(user)
            .accountStatus(AccountStatus.ACTIVATED)
            .accountNumber(createAccountNumber())
            .balance(0L)
            .registeredAt(LocalDateTime.now())
            .build()));
  }

  private String createAccountNumber() {
    return accountRepository.findFirstByOrderByIdDesc()
        .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1
            + "")
        .orElse("1000000000");
  }

  private void checkAccountLimit_5(UserEntity user) {
    if (accountRepository.countByUser(user) >= 5) {
      throw new GlobalException(ErrorCode.MAX_5_LIMIT_ACCOUNTS);
    }
  }

  @Transactional
  public AccountDto deleteAccount(Long userId, String accountNumber) {
    UserEntity accountUser = userRepository.findById(userId)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    AccountEntity account = accountRepository.findByAccountNumber(
            accountNumber)
        .orElseThrow(() -> new GlobalException(ACCOUNT_NOT_FOUND));

    validateDeleteAccount(accountUser, account);

    account.setAccountStatus(AccountStatus.UNREGISTERED);
    account.setUnRegisteredAt(LocalDateTime.now());

    // 불필요하지만 테스트를 위해 넣어둠
    accountRepository.save(account);

    return AccountDto.fromEntity(account);
  }

  private void validateDeleteAccount(
      UserEntity accountUser, AccountEntity account)
      throws GlobalException {
    if (!Objects.equals(accountUser.getId(), account.getUser().getId())) {
      throw new GlobalException(ErrorCode.USER_ACCOUNT_UNMATCHED);
    }
    if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
      throw new GlobalException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
    }
    if (account.getBalance() > 0) {
      throw new GlobalException(ErrorCode.BALANCE_NOT_EMPTY);
    }
  }
}

