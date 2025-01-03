package com.bj.convo.global.jwt.provider;

import com.bj.convo.global.jwt.model.JwtToken;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final int accessTokenExpiredTime;
    private final int refreshTokenExpiredTime;
    private final JwtParser jwtParser;

    public JwtTokenProvider(@Value("${jwt.key}") String key,
                            @Value("${jwt.access-expired-time}") int accessTokenExpiredTime,
                            @Value("${jwt.refresh-expired-time}") int refreshTokenExpiredTime) {
        log.info(key);
        this.key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        this.accessTokenExpiredTime = accessTokenExpiredTime;
        this.refreshTokenExpiredTime = refreshTokenExpiredTime;
        this.jwtParser = Jwts.parser()
                .verifyWith(this.key)
                .build();
    }

    public JwtToken generateToken(Long userId) {
        return JwtToken.builder()
                .accessToken(generateAccessToken(userId))
                .refreshToken(generateRefreshToken(userId))
                .build();
    }

    private String generateAccessToken(Long userId) {
        return Jwts.builder()
                .claim("uid", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiredTime))
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .claim("uid", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiredTime))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        return true;
        // 만료
        // 올바른
    }

    public void getToken(String accessToken) {
        String payload = jwtParser.parse(accessToken).getPayload().toString();
        log.warn(payload);
    }
}
