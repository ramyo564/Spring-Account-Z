package com.example.accountz.exception;


import com.example.accountz.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ErrorResponse handleUserException(GlobalException e){
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
        return new ErrorResponse(
                ErrorCode.WRONG_VALIDATION,
                "날짜 형식이 맞지 않습니다. " +
                        "MM/dd/yyyy 에 맞게 작성해주세요"
                );

    }
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e){
        log.error("Exception is occurred", e);
        return new ErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                e.getMessage()
        );
    }
}
