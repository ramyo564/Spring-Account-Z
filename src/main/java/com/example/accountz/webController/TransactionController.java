package com.example.accountz.webController;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.UseBalanceDto;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("save-money")
  public UseBalanceDto.Response userSaveMoney(
      @Valid @RequestBody UseBalanceDto.Request request
  ){
    try {
      return UseBalanceDto.Response.from(transactionService.saveMoney(
          JwtTokenExtract.currentUser().getId(),
          request.getAccountNumber(),
          request.getAmount()));

    } catch (GlobalException e) {
      log.error("Failed to use balance.");

      transactionService.saveFailedUseTransaction(
          request.getAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

}
