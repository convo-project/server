package com.bj.convo.global.security.filter;

import com.bj.convo.domain.user.model.dto.request.LoginRequest;
import com.bj.convo.global.jwt.model.JwtToken;
import com.bj.convo.global.jwt.provider.JwtTokenProvider;
import com.bj.convo.global.security.exception.SecurityErrorCode;
import com.bj.convo.global.security.service.UserDetailsImpl;
import com.bj.convo.global.util.redis.RedisUtil;
import com.bj.convo.global.util.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@Slf4j
public class UsernamePasswordFilter extends AbstractAuthenticationProcessingFilter {

    private static final String LOGIN_REQUEST_URL = "/api/user/login";
    private static final String HTTP_METHOD = "POST";
    private static final String HTTP_METHOD_ERROR_MESSAGE = "POST 메소드 외에는 지원하지 않습니다.";
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    private static final AntPathRequestMatcher LOGIN_REQUEST_MATCHER =
            new AntPathRequestMatcher(LOGIN_REQUEST_URL, HTTP_METHOD);

    public UsernamePasswordFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper,
                                  JwtTokenProvider jwtTokenProvider, RedisUtil redisUtil) {
        super(LOGIN_REQUEST_MATCHER, authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisUtil = redisUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String method = request.getMethod();

        log.info("Start JwtFilter");

        if (!method.equals("POST")) {
            throw new HttpRequestMethodNotSupportedException(HTTP_METHOD_ERROR_MESSAGE);
        }

        ServletInputStream inputStream = request.getInputStream();

        LoginRequest loginRequest = objectMapper.readValue(inputStream, LoginRequest.class);

        return this.getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                ));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        JwtToken jwtToken = jwtTokenProvider.generateToken(userDetails.getUserId());

        Cookie accessTokenCookie = new Cookie("access_token", jwtToken.getAccessToken());
        Cookie refreshTokenCookie = new Cookie("refresh_token", jwtToken.getRefreshToken());

        String redisRefreshTokenPrefix = "refresh_token:" + userDetails.getUserId();

        redisUtil.setData(redisRefreshTokenPrefix, refreshTokenCookie.getValue(), jwtTokenProvider.getRefreshTokenExpiredTime());

        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        // TODO: https 설정 이후
//        refreshTokenCookie.setSecure(true);

        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(jwtToken));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        ErrorResponse error = ErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(SecurityErrorCode.NOT_EXIST_EMAIL_OR_PASSWORD.getMessage())
                .build();
        log.error("로그인 실패");
        response.setStatus(SecurityErrorCode.NOT_EXIST_EMAIL_OR_PASSWORD.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
