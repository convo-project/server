package com.bj.convo.global.security.model;

import com.bj.convo.domain.user.model.entity.Users;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Users users;
    private Map<String, Object> attributes;

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    public CustomUserDetails(Users users, Map<String, Object> attributes) {
        this.users = users;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(() -> "ROLE_USERS");
        return collectors;
    }

    @Override
    public String getPassword() {
        return users.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }

    public Long getUserId() {
        return users.getId();
    }

    @Override
    public String getName() {
        return users.getNickname();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
