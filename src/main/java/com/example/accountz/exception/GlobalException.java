package com.example.accountz.exception;

import com.example.accountz.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}


