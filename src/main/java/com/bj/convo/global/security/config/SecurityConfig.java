package com.bj.convo.global.security.config;

import com.bj.convo.domain.user.repository.UsersRepository;
import com.bj.convo.global.security.exception.CustomAuthenticationEntryPoint;
import com.bj.convo.global.security.filter.ExceptionHandlerFilter;
import com.bj.convo.global.security.filter.JwtFilter;
import com.bj.convo.global.security.filter.UsernamePasswordFilter;
import com.bj.convo.global.jwt.provider.JwtTokenProvider;
import com.bj.convo.global.security.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersRepository usersRepository;

    public final static String[] allowedUrls = {
            "/api/user/register",
            "/api/user/login",
            "/error",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs"
    };

    @Value("${spring.security.debug:false}")
    boolean isSecurityDebug;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(allowedUrls).permitAll()// 임시
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(exceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(usernamePasswordLoginFilter(), ExceptionHandlerFilter.class)
                .addFilterAfter(jwtFilter(), UsernamePasswordFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint()))
        ;

        return http.build();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(isSecurityDebug);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }

    @Bean
    public UsernamePasswordFilter usernamePasswordLoginFilter() throws Exception {
        UsernamePasswordFilter usernamePasswordFilter = new UsernamePasswordFilter(
                authenticationManager(), objectMapper, jwtTokenProvider);
        usernamePasswordFilter.setAuthenticationManager(authenticationManager());
        return usernamePasswordFilter;
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter() throws Exception {
        return new ExceptionHandlerFilter(objectMapper);
    }

    @Bean
    public JwtFilter jwtFilter() throws Exception {
        return new JwtFilter(jwtTokenProvider, usersRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}
