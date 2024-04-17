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

    //
    INTERNAL_SERVER_ERROR("내부 서버 오류")
    ;
    private final String description;
}
