package com.bj.convo.global.security.filter;

import com.bj.convo.domain.user.exception.UsersErrorCode;
import com.bj.convo.domain.user.model.entity.Users;
import com.bj.convo.domain.user.repository.UsersRepository;
import com.bj.convo.global.jwt.provider.JwtTokenProvider;
import com.bj.convo.global.security.config.SecurityConfig;
import com.bj.convo.global.security.exception.SecurityErrorCode;
import com.bj.convo.global.security.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.security.sasl.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UsersRepository usersRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> excludeUrlPatterns = Arrays.asList(SecurityConfig.allowedUrls);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        return excludeUrlPatterns.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, AuthenticationException {
        String token = resolveBearerToken(request);

        try {
            if (StringUtils.hasText(token) || jwtTokenProvider.validateToken(token)) {
                forceAuthentication(token);
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveBearerToken(HttpServletRequest request) throws AuthenticationException {
        String bearerToken = request.getHeader("Authorization");
        log.info(bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private Users getUsersFromToken(String token) throws AuthenticationException {
        Long userId = jwtTokenProvider.getSubject(token);

        return usersRepository.findById(userId).orElseThrow(() ->
                new AuthenticationException(UsersErrorCode.NOT_EXIST_USER.getMessage()));
    }

    private void forceAuthentication(String token) throws AuthenticationException {
        Users user = getUsersFromToken(token);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info(authentication.getName());
    }
}
