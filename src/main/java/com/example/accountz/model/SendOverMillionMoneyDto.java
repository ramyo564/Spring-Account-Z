package com.example.accountz.model;

import com.example.accountz.type.TransactionResultType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

public class SendOverMillionMoneyDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

    @NotBlank
    @Size(min = 10, max = 10)
    private String userAccountNumber;

    @NotBlank
    @Size(min = 2, message = "이름은 두 글자 이상 입력해주세요")
    private String userName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    @Past(message = "미래의 날짜는 불가능 합니다.")
    private LocalDate birthDay;

    @NotNull
    @NotBlank
    @Email(message = "올바른 이메일 주소를 입력해주세요")
    private String email;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 8자리 이상 입력해주세요")
    private String password;

    @NotBlank
    @Size(min = 10, max = 10)
    private String receiverAccountNumber;

    @NotNull
    @Min(1000_000)
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
