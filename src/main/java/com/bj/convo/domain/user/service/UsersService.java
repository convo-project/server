package com.bj.convo.domain.user.service;

import com.bj.convo.domain.user.exception.UsersErrorCode;
import com.bj.convo.domain.user.model.dto.request.RegisterRequest;
import com.bj.convo.domain.user.model.entity.Users;
import com.bj.convo.domain.user.repository.UsersRepository;
import com.bj.convo.global.common.exception.RestApiException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest req) {
        if (usersRepository.existsByEmail(req.email())) {
            throw new RestApiException(UsersErrorCode.ALREADY_EXIST_EMAIL);
        }

        String encryptedPassword = passwordEncoder.encode(req.password());
        Users entity = req.toEntity(encryptedPassword);
        usersRepository.save(entity);
    }
}
