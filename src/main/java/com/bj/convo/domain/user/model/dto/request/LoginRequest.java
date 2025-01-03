package com.bj.convo.domain.user.model.dto.request;

import lombok.Builder;

@Builder
public record LoginRequest(
        String email,
        String password
) {
}
