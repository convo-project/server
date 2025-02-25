package com.bj.convo.global.security.filter;

import com.bj.convo.domain.user.exception.UsersErrorCode;
import com.bj.convo.domain.user.model.entity.Users;
import com.bj.convo.domain.user.repository.UsersRepository;
import com.bj.convo.global.jwt.model.JwtToken;
import com.bj.convo.global.jwt.provider.JwtTokenProvider;
import com.bj.convo.global.config.SecurityConfig;
import com.bj.convo.global.security.exception.SecurityErrorCode;
import com.bj.convo.global.security.service.UserDetailsImpl;
import com.bj.convo.global.util.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String REISSUE_REQUEST_URL = "/api/user/reissue";

    private final JwtTokenProvider jwtTokenProvider;
    private final UsersRepository usersRepository;
    private final RedisUtil redisUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> excludeUrlPatterns = Arrays.asList(SecurityConfig.allowedUrls);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return excludeUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveBearerToken(request);
        Claims payload = jwtTokenProvider.getPayload(token);
        String tokenType = payload.getSubject();

        if (pathMatcher.match(REISSUE_REQUEST_URL, request.getRequestURI())) {
            if (tokenType.equals("refresh_token")) {
                reissueAccessToken(Long.parseLong(payload.get("user_id", String.class)), response);
            } else {
                throw new JwtException(SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getMessage());
            }

        } else {
            if (tokenType.equals("access_token")) {
                forceAuthentication(payload);
                filterChain.doFilter(request, response);
            } else {
                throw new JwtException(SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getMessage());
            }
        }
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }

        throw new JwtException(SecurityErrorCode.MALFORMED_TOKEN.getMessage());
    }

    private void forceAuthentication(Claims payload) {
        Users user = getUsersFromPayload(payload);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Users getUsersFromPayload(Claims payload) {
        Long userId = Long.parseLong(payload.get("user_id", String.class));

        return usersRepository.findById(userId).orElseThrow(() ->
                new JwtException(UsersErrorCode.NOT_EXIST_USER.getMessage()));
    }

    private void reissueAccessToken(Long userId, HttpServletResponse response) {
        if (!usersRepository.existsById(userId)) {
            throw new JwtException(UsersErrorCode.NOT_EXIST_USER.getMessage());
        }
        JwtToken jwtToken = jwtTokenProvider.generateToken(userId);
        setResponse(response, userId, jwtToken);
    }

    private void setResponse(HttpServletResponse response, Long userId, JwtToken jwtToken) {
        Cookie accessTokenCookie = new Cookie("access_token", jwtToken.getAccessToken());
        Cookie refreshTokenCookie = new Cookie("refresh_token", jwtToken.getRefreshToken());

        String redisRefreshTokenPrefix = "refresh_token:" + userId;

        redisUtil.setData(redisRefreshTokenPrefix, refreshTokenCookie.getValue(), jwtTokenProvider.getRefreshTokenExpiredTime());

        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);

        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.setStatus(200);
    }
}
