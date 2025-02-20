package com.bj.convo.global.security.filter;

import com.bj.convo.global.security.exception.SecurityErrorCode;
import com.bj.convo.global.util.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.security.sasl.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            if (e.getMessage().equals(SecurityErrorCode.EXPIRED_TOKEN.getMessage())) {
                setErrorResponse(SecurityErrorCode.EXPIRED_TOKEN.getHttpStatus(),
                        SecurityErrorCode.EXPIRED_TOKEN.getMessage(), request, response);
            } else {
                setErrorResponse(SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getHttpStatus(),
                        SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getMessage(), request, response);
            }
        } catch (Exception e) {
            log.error("알 수 없는 예외 발생: {}", e.getMessage(), e);
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버가 사용자의 요청을 처리하는 과정에서 내부 오류가 발생했습니다.", request,
                    response);
        }
    }

    private void setErrorResponse(HttpStatus status, String message, HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(status.getReasonPhrase())
                .message(message)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.flushBuffer();
    }
}
