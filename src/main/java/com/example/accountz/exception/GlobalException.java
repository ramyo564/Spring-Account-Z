package com.example.accountz.exception;

import com.example.accountz.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}


