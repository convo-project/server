package com.bj.convo.global.security.filter;

import com.bj.convo.global.security.exception.SecurityErrorCode;
import com.bj.convo.global.util.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
            log.error("토큰 만료: {}", e.getMessage(), e);
            if (e.getMessage().equals(SecurityErrorCode.EXPIRED_TOKEN.getMessage())) {
                setErrorResponse(SecurityErrorCode.EXPIRED_TOKEN.getHttpStatus(),
                        SecurityErrorCode.EXPIRED_TOKEN.getMessage(), request, response);
            } else {
                log.error("토큰 오류: {}", e.getMessage(), e);
                setErrorResponse(SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getHttpStatus(),
                        SecurityErrorCode.UNKNOWN_TOKEN_ERROR.getMessage(), request, response);
            }
        } catch (Exception e) {
            log.error("알 수 없는 예외 발생: {}", e.getMessage(), e);
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", request, response);
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
