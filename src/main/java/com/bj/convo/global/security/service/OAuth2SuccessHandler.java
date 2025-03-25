package com.bj.convo.global.security.service;

import static com.bj.convo.global.config.CorsConfig.allowedDomain;
import static com.bj.convo.global.security.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.bj.convo.global.jwt.model.JwtToken;
import com.bj.convo.global.jwt.provider.JwtTokenProvider;
import com.bj.convo.global.security.model.CustomUserDetails;
import com.bj.convo.global.util.cookie.CookieUtil;
import com.bj.convo.global.util.redis.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        JwtToken jwtToken = jwtTokenProvider.generateToken(userDetails.getUserId());

        String redisRefreshTokenName = "refresh_token:" + userDetails.getUserId();

        redisUtil.setData(redisRefreshTokenName, jwtToken.getRefreshToken(),
                jwtTokenProvider.getRefreshTokenExpiredTime());

        response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());

        CookieUtil.addCookie(response, "refresh_token", jwtToken.getRefreshToken(),
                (int) (jwtTokenProvider.getRefreshTokenExpiredTime() / 1000));

        String targetUrl = UriComponentsBuilder.fromUriString(determineTargetUrl(request, response, authentication))
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUrl.orElse(getDefaultTargetUrl());

        try {
            URI uri = new URI(targetUrl);
            String normalizedTargetUrl = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() == -1 ? "" : ":" + uri.getPort());

            if (!Arrays.asList(allowedDomain).contains(normalizedTargetUrl)) {
                //TODO:
                throw new IllegalArgumentException("허용되지 않은 리다이렉트 URI입니다: " + targetUrl);
            }

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("잘못된 URI 형식입니다: " + targetUrl, e);
        }

        return UriComponentsBuilder.fromUriString(targetUrl).toUriString();
    }
}