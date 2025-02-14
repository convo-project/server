package com.bj.convo.domain.user.exception;

import com.bj.convo.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UsersErrorCode implements ErrorCode {
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    NOT_VERIFIED_EMAIL(HttpStatus.UNAUTHORIZED, "인증되지 않은 이메일입니다."),
    SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 문제가 발생했습니다."),
    INCORRECT_VERIFY_CODE(HttpStatus.BAD_REQUEST, "올바르지 않은 인증 코드입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
