package com.example.accountz.model;


import com.example.accountz.persist.entity.TransactionEntity;
import com.example.accountz.type.TransactionResultType;
import com.example.accountz.type.TransactionType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

  private String accountNumber;
  private TransactionType transactionType;
  private TransactionResultType transactionResultType;
  private Long amount;
  private Long balanceSnapshot;
  private String transactionId;
  private LocalDateTime transactedAt;

  public static TransactionDto fromEntity(TransactionEntity transaction){
    return TransactionDto.builder()
        .accountNumber(transaction.getAccount().getAccountNumber())
        .transactionType(transaction.getTransactionType())
        .transactionResultType(transaction.getTransactionResultType())
        .amount(transaction.getAmount())
        .balanceSnapshot(transaction.getBalanceSnapshot())
        .transactionId(transaction.getTransactionId())
        .transactedAt(transaction.getTransactedAt())
        .build();
  }
}
