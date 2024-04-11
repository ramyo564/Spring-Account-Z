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

    //
    INTERNAL_SERVER_ERROR("내부 서버 오류")
    ;
    private final String description;
}
