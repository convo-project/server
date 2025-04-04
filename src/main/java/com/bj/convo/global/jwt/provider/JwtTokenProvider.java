package com.bj.convo.global.jwt.provider;

import com.bj.convo.global.jwt.model.JwtToken;
import com.bj.convo.global.security.exception.SecurityErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final Long accessTokenExpiredTime;
    @Getter
    private final Long refreshTokenExpiredTime;
    private final JwtParser jwtParser;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String key,
                            @Value("${spring.jwt.access-expired-time}") Long accessTokenExpiredTime,
                            @Value("${spring.jwt.refresh-expired-time}") Long refreshTokenExpiredTime) {
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
                .claim("user_id", userId.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiredTime))
                .subject("access_token")
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .claim("user_id", userId.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiredTime))
                .subject("refresh_token")
                .signWith(key)
                .compact();
    }

    public Claims getPayload(String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            if (e instanceof SignatureException) {
                log.error(SecurityErrorCode.SIGNATURE_FAILED_TOKEN.getMessage());
                throw new JwtException(SecurityErrorCode.SIGNATURE_FAILED_TOKEN.getMessage());
            } else if (e instanceof ExpiredJwtException) {
                log.error(SecurityErrorCode.EXPIRED_TOKEN.getMessage());
                throw new JwtException(SecurityErrorCode.EXPIRED_TOKEN.getMessage());
            } else if (e instanceof MalformedJwtException) {
                log.error(SecurityErrorCode.MALFORMED_TOKEN.getMessage());
                throw new JwtException(SecurityErrorCode.MALFORMED_TOKEN.getMessage());
            } else {
                log.error(SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getMessage());
                throw new JwtException(SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getMessage());
            }
        }
    }
}
