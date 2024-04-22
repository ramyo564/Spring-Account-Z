package com.example.accountz.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // User
  USER_NOT_FOUND("사용자가 없습니다."),
  ALREADY_REGISTERED_EMAIL("이미 등록된 이메일 입니다."),
  WRONG_PASSWORD("잘못된 비밀번호 입니다."),
  WRONG_VALIDATION("상황에 맞게 내부 메세지에서 캐치 및 변경"),

  // Token
  WRONG_TOKEN("토큰 정보가 유효하지 않습니다."),

  // Account
  MAX_5_LIMIT_ACCOUNTS("최대 계좌 생성은 5개까지 입니다."),
  ACCOUNT_NOT_FOUND("계좌가 없습니다."),
  USER_ACCOUNT_UNMATCHED("사용자와 계좌의 소유주가 다릅니다."),
  ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
  BALANCE_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
  RECEIVER_ACCOUNT_UNREGISTERED("받는 이의 계좌가 해지되어 송금이 불가능 합니다."),

  // Transaction
  AMOUNT_EXCEED_BALANCE("거래금액이 잔액을 초과했습니다."),
  NOT_MINUS_MONEY("0보다 작은 금액은 입금은 불가능합니다."),
  UNMATCHED_USER("이름이 일치하지 않습니다."),
  UNMATCHED_BIRTHDAY("생년월일이 일치하지 않습니다."),
  UNMATCHED_EMAIL("생년월일이 일치하지 않습니다."),
  UNMATCHED_PASSWORD("패스워드가 일치하지 않습니다."),
  TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),
  NOT_ALLOWED_SEPARATE_CANCEL("부분 취소는 허용되지 않습니다."),
  TRANSACTION_ACCOUNT_UNMATCHED("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),
  TOO_OLD_ORDER_TO_CANCEL("1년이지난 거래는 취소가 불가능합니다."),
  INVALID_REQUEST("잘못된 요청입니다."),
  EXPIRED_TRANSACTION("이미 취소가 된 거래입니다."),
  WRONG_DATE("시작 날짜는 종료 날짜보다 이전이어야 합니다."),

  //
  INTERNAL_SERVER_ERROR("내부 서버 오류");
  private final String description;
}
