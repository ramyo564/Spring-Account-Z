package com.example.accountz.model;

import com.example.accountz.type.TransactionResultType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CancelBalanceDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotBlank
    private String transactionId;

    @NotBlank
    @Size(min = 10, max = 10)
    private String userAccountNumber;

    @NotBlank
    @Size(min = 10, max = 10)
    private String receiverAccountNumber;

    @NotNull
    @Min(1)
    @Max(1000_000_000)
    private Long amount;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private String userAccountNumber;
    private String receiverAccountNumber;
    private TransactionResultType transactionResultType;
    private String transactionId;
    private Long amount;
    private LocalDateTime transactionAt;


    public static Response from(TransactionDto transactionDto) {
      return Response.builder()
          .userAccountNumber(transactionDto.getUserAccountNumber())
          .receiverAccountNumber(transactionDto.getReceiverAccountNumber())
          .transactionResultType(transactionDto.getTransactionResultType())
          .transactionId(transactionDto.getTransactionId())
          .amount(transactionDto.getAmount())
          .transactionAt(transactionDto.getTransactedAt())
          .build();
    }
  }

}
