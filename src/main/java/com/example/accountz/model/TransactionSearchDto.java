package com.example.accountz.model;

import com.example.accountz.persist.entity.TransactionEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionSearchDto {
  private String accountNumber;
  private Long amount;
  private LocalDate date;
  private String sender;
  private String receiver;

  public static TransactionSearchDto fromEntity(TransactionEntity transaction){
    return TransactionSearchDto.builder()
        .accountNumber(transaction.getAccount().getAccountNumber())
        .amount(transaction.getAmount())
        .date(transaction.getTransactedAt().toLocalDate())
        .sender(transaction.getUser().getName())
        .receiver(transaction.getReceiver())
        .build();
  }
}

