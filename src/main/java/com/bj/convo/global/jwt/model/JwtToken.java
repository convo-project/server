package com.bj.convo.global.jwt.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtToken {
    private String accessToken;
    private String refreshToken;
}
