package com.bj.convo.global.security.model;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OAuth2UserInfo {
    private String email;
    private String password;
    private String nickname;
    private String provider;

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> ofGoogle(attributes);
            default -> throw new RuntimeException();
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .email((String) attributes.get("email"))
                .password((String) attributes.get("sub"))
                .nickname((String) attributes.get("name"))
                .provider("GOOGLE")
                .build();
    }
}
