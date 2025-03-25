package com.bj.convo.global.security.service;

import com.bj.convo.domain.user.model.entity.Users;
import com.bj.convo.domain.user.repository.UsersRepository;
import com.bj.convo.global.security.model.CustomUserDetails;
import com.bj.convo.global.security.model.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsersRepository usersRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.getAttributes());

        Users user = usersRepository.findByEmail(oAuth2UserInfo.getEmail()).orElse(null);
        if (user == null) {
            user = usersRepository.save(Users.builder()
                    .email(oAuth2UserInfo.getEmail())
                    .nickname(oAuth2UserInfo.getNickname())
                    .encryptedPassword(oAuth2UserInfo.getPassword())
                    .provider(oAuth2UserInfo.getProvider())
                    .build());
        } else if (user.getProvider().equals("LOCAL")) {
            user.updateProvider(oAuth2UserInfo.getProvider());
            user = usersRepository.save(user);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}

// 이미 가입된 사용자, 또는 일반 회원가입으로 로그인한 OAuth2 유저는 기존 계정을 OAuth2로 변경