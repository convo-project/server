package com.bj.convo.domain.user.model.dto.request;

public record VerifyEmailRequest(
        String email,
        String verifyCode
) {
}
