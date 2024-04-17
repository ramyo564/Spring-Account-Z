package com.example.accountz.exception;


import com.example.accountz.type.ErrorCode;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(GlobalException.class)
  public ErrorResponse handleUserException(GlobalException e) {
    log.error("{} is occurred", e.getErrorCode());
    return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    return new ErrorResponse(
        ErrorCode.WRONG_VALIDATION,
        Objects.requireNonNull(
            e.getBindingResult()
                .getFieldError()).getDefaultMessage()
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ErrorResponse handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    return new ErrorResponse(ErrorCode.WRONG_VALIDATION, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ErrorResponse handleException(Exception e) {
    log.error("Exception is occurred", e);
    return new ErrorResponse(
        ErrorCode.INTERNAL_SERVER_ERROR,
        e.getMessage()
    );
  }
}
