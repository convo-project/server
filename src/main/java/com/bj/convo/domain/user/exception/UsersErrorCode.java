package com.bj.convo.domain.user.exception;

import com.bj.convo.global.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UsersErrorCode implements ErrorCode {
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
