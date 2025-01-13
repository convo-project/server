package com.bj.convo.global.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode {
    NOT_EXIST_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "올바르지 않은 이메일 또는 비밀번호입니다."),
    SIGNATURE_FAILED_TOKEN(HttpStatus.UNAUTHORIZED, "검증에 실패한 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "토큰 파싱 중 에러가 발생했습니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "손상된 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
