package com.example.accountz.webController;

import com.example.accountz.exception.GlobalException;
import com.example.accountz.model.AccountInfoDto;
import com.example.accountz.model.CancelBalanceDto;
import com.example.accountz.model.SendOverMillionMoneyDto;
import com.example.accountz.model.SendUnderMillionMoneyDto;
import com.example.accountz.model.TransactionBetweenDateDto;
import com.example.accountz.model.TransactionNameInfoDto;
import com.example.accountz.model.TransactionSearchDto;
import com.example.accountz.model.UseBalanceDto;
import com.example.accountz.security.JwtTokenExtract;
import com.example.accountz.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final JwtTokenExtract jwtTokenExtract;

  @PreAuthorize("hasRole('USER')")
  @PostMapping("save-money")
  public UseBalanceDto.Response userSaveMoney(
      @Valid @RequestBody UseBalanceDto.Request request
  ) {
    try {
      return UseBalanceDto.Response.from(transactionService.saveMoney(
          jwtTokenExtract.currentUser().getId(),
          request.getAccountNumber(),
          request.getAmount()));

    } catch (GlobalException e) {
      log.error("Failed to save balance.");

      transactionService.saveFailedUseTransaction(
          jwtTokenExtract.currentUser(),
          request.getAccountNumber(),
          request.getAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("withdraw")
  public UseBalanceDto.Response useBalance(
      @Valid @RequestBody UseBalanceDto.Request request
  ) {
    try {
      return UseBalanceDto.Response.from(transactionService.useBalance(
          jwtTokenExtract.currentUser().getId(),
          request.getAccountNumber(),
          request.getAmount()));

    } catch (GlobalException e) {
      log.error("Failed to use balance.");
      transactionService.saveFailedUseTransaction(
          jwtTokenExtract.currentUser(),
          request.getAccountNumber(),
          request.getAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("under-million-send-money")
  public SendUnderMillionMoneyDto.Response underMillionSendMoney(
      @Valid @RequestBody SendUnderMillionMoneyDto.Request request
  ) {
    try {
      return SendUnderMillionMoneyDto.Response.from(
          transactionService.underMillionSendMoney(
              jwtTokenExtract.currentUser().getId(),
              request.getUserAccountNumber(),
              request.getReceiverAccountNumber(),
              request.getAmount()));

    } catch (GlobalException e) {
      log.error("Failed to use balance.");
      transactionService.saveFailedUseTransaction(
          jwtTokenExtract.currentUser(),
          request.getUserAccountNumber(),
          request.getReceiverAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("over-million-send-money")
  public SendOverMillionMoneyDto.Response overMillionSendMoney(
      @Valid @RequestBody SendOverMillionMoneyDto.Request request
  ) {
    try {
      return SendOverMillionMoneyDto.Response.from(
          transactionService.overMillionSendMoney(
              jwtTokenExtract.currentUser().getId(),
              request.getUserAccountNumber(),
              request.getUserName(),
              request.getBirthDay(),
              request.getEmail(),
              request.getPassword(),
              request.getReceiverAccountNumber(),
              request.getAmount()));

    } catch (GlobalException e) {
      log.error("Failed to use balance.");
      transactionService.saveFailedUseTransaction(
          jwtTokenExtract.currentUser(),
          request.getUserAccountNumber(),
          request.getReceiverAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/cancel-transaction")
  public CancelBalanceDto.Response cancelBalance(
      @Valid @RequestBody CancelBalanceDto.Request request
  ) {
    try {
      return CancelBalanceDto.Response.from(
          transactionService.cancelBalance(
              jwtTokenExtract.currentUser().getId(),
              request.getTransactionId(),
              request.getUserAccountNumber(),
              request.getReceiverAccountNumber(),
              request.getAmount()));

    } catch (GlobalException e) {
      log.error("Failed to use balance.");
      transactionService.saveFailedUseTransaction(
          jwtTokenExtract.currentUser(),
          request.getUserAccountNumber(),
          request.getReceiverAccountNumber(),
          request.getAmount()
      );
      throw e;
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/get-transaction")
  public List<TransactionSearchDto> getTransaction() {
    return transactionService.getTransaction(
            jwtTokenExtract.currentUser().getId())
        .stream().map(transactionSearchDto ->
            TransactionSearchDto.builder()
                .accountNumber(transactionSearchDto.getAccountNumber())
                .amount(transactionSearchDto.getAmount())
                .date(transactionSearchDto.getDate())
                .sender(transactionSearchDto.getSender())
                .receiver(transactionSearchDto.getReceiver())
                .build())
        .collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/get-transaction-order-by-receive-money")
  public List<TransactionSearchDto> getOrderByReceiveMoney(
      @Valid @RequestBody AccountInfoDto request
  ) {
    return transactionService.getOrderByReceiveMoney(
            request.getAccountNumber())
        .stream().map(transactionSearchDto ->
            TransactionSearchDto.builder()
                .accountNumber(transactionSearchDto.getAccountNumber())
                .amount(transactionSearchDto.getAmount())
                .date(transactionSearchDto.getDate())
                .sender(transactionSearchDto.getSender())
                .receiver(transactionSearchDto.getReceiver())
                .build())
        .collect(Collectors.toList());
  }
  @PreAuthorize("hasRole('USER')")
  @PostMapping("/get-transaction-between-date")
  public List<TransactionSearchDto> getOrderByDateBetweenDateAndDate(
      @Valid @RequestBody TransactionBetweenDateDto.Request request
  ) {
    return transactionService.getBetweenDate(
        jwtTokenExtract.currentUser().getId(),
        request.getFirstDate(),
        request.getLastDate())
        .stream().map(transactionSearchDto ->
            TransactionSearchDto.builder()
                .accountNumber(transactionSearchDto.getAccountNumber())
                .amount(transactionSearchDto.getAmount())
                .date(transactionSearchDto.getDate())
                .sender(transactionSearchDto.getSender())
                .receiver(transactionSearchDto.getReceiver())
                .build())
        .collect(Collectors.toList());

  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/get-transaction-order-by-person")
  public List<TransactionSearchDto> getOrderByName() {
    return transactionService.getOrderByName(
            jwtTokenExtract.currentUser().getId())
        .stream().map(transactionSearchDto ->
            TransactionSearchDto.builder()
                .accountNumber(transactionSearchDto.getAccountNumber())
                .amount(transactionSearchDto.getAmount())
                .date(transactionSearchDto.getDate())
                .sender(transactionSearchDto.getSender())
                .receiver(transactionSearchDto.getReceiver())
                .build())
        .collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/get-fail-transaction")
  public List<TransactionSearchDto> getFailTransaction() {
    return transactionService.getFailTransaction(
            jwtTokenExtract.currentUser().getId())
        .stream().map(transactionSearchDto ->
            TransactionSearchDto.builder()
                .accountNumber(transactionSearchDto.getAccountNumber())
                .amount(transactionSearchDto.getAmount())
                .date(transactionSearchDto.getDate())
                .sender(transactionSearchDto.getSender())
                .receiver(transactionSearchDto.getReceiver())
                .build())
        .collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/get-receiver-transaction")
  public List<TransactionSearchDto> getReceiverTransaction(
      @Valid @RequestBody TransactionNameInfoDto.Request request
  ) {
    return transactionService.getReceiverTransaction(
            jwtTokenExtract.currentUser().getId(), request.getName())
        .stream().map(transactionSearchDto ->
            TransactionSearchDto.builder()
                .accountNumber(transactionSearchDto.getAccountNumber())
                .amount(transactionSearchDto.getAmount())
                .date(transactionSearchDto.getDate())
                .sender(transactionSearchDto.getSender())
                .receiver(transactionSearchDto.getReceiver())
                .build())
        .collect(Collectors.toList());
  }
}
