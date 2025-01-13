package com.bj.convo.global.security.filter;

import com.bj.convo.global.util.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
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
            log.info("시작!!!!");
            filterChain.doFilter(request, response);
            log.info("종료!!!!");
        } catch (HttpRequestMethodNotSupportedException
                 | MalformedJwtException
                 | ExpiredJwtException
                 | SignatureException
                e) {
            setErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request, response);
            return;
        } catch (AuthenticationException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, response);
            return;
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
