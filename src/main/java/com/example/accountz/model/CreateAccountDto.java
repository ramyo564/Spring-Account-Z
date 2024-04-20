package com.example.accountz.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CreateAccountDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotNull
    @Min(1)
    private Long userId;

    @NotNull
    @Min(0)
    private Long initialBalance;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private Long userId;
    private String accountNumber;
    private LocalDateTime registeredAt;

    public static Response from(AccountDto accountDto) {
      return Response.builder()
          .userId(accountDto.getUserId())
          .accountNumber(accountDto.getAccountNumber())
          .registeredAt(accountDto.getRegisteredAt())
          .build();
    }
  }
}