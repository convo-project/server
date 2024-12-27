package com.bj.convo.domain.user.model.dto.request;

import com.bj.convo.domain.user.model.entity.Users;

public record RegisterRequest(
        String email,
        String password,
        String nickname
) {
    public Users toEntity() {
        return Users.builder()
                .nickname(this.nickname)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
