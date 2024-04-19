package com.example.accountz.model;

import com.example.accountz.persist.entity.AccountEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
  private Long userId;
  private String accountNumber;
  private Long balance;

  private LocalDateTime registeredAt;
  private LocalDateTime unRegisteredAt;

  public static AccountDto fromEntity(AccountEntity account){
    return AccountDto.builder()
        .userId(account.getId())
        .accountNumber(account.getAccountNumber())
        .balance(account.getBalance())
        .registeredAt(account.getRegisteredAt())
        .unRegisteredAt(account.getUnRegisteredAt())
        .build();
  }

}
