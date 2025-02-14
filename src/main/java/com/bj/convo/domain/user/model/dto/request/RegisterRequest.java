package com.bj.convo.domain.user.model.dto.request;

import com.bj.convo.domain.user.model.entity.Users;

public record RegisterRequest(
        String email,
        String password,
        String nickname,
        boolean isVerified
) {
    public Users toEntity(String encryptedPassword) {
        return Users.builder()
                .nickname(nickname)
                .email(email)
                .encryptedPassword(encryptedPassword)
                .build();
    }
}
