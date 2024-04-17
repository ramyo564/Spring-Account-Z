package com.example.accountz.service;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  @Transactional
  public AccountDto createAccount() {
    Long userId = JwtTokenExtract.currentUser().getId();

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() ->
            new GlobalException(ErrorCode.USER_NOT_FOUND));

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
        .map(account -> (
            Integer.parseInt(
                account
                    .getAccountNumber())) + 1 + "")
        .orElse("1000000000");
  }

  private void checkAccountLimit_5(UserEntity user) {
    if (accountRepository.countByUser(user) >= 5) {
      throw new GlobalException(ErrorCode.MAX_5_LIMIT_ACCOUNTS);
    }
  }


}
