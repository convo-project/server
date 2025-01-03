package com.bj.convo.global.security.service;

import com.bj.convo.domain.user.model.entity.Users;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Users users;

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
}
