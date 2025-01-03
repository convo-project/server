package com.bj.convo.global.security.filter;

import com.bj.convo.global.util.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (HttpRequestMethodNotSupportedException e) {
            setErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request, response);
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
    }
}
