package com.example.accountz.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DeleteAccountDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotBlank
    @Size(min = 10, max = 10)
    private String accountNumber;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private Long userId;
    private String accountNumber;
    private LocalDateTime unRegisteredAt;

    public static Response from(AccountDto accountDto) {

      return Response.builder()
          .userId(accountDto.getUserId())
          .accountNumber(accountDto.getAccountNumber())
          .unRegisteredAt(accountDto.getUnRegisteredAt())
          .build();
    }
  }
}
