package com.bj.convo.domain.user.service;

import com.bj.convo.domain.user.exception.UsersErrorCode;
import com.bj.convo.domain.user.model.dto.request.RegisterRequest;
import com.bj.convo.domain.user.model.dto.request.VerifyEmailRequest;
import com.bj.convo.domain.user.model.entity.Users;
import com.bj.convo.domain.user.repository.UsersRepository;
import com.bj.convo.global.common.exception.RestApiException;
import com.bj.convo.global.email.service.EmailService;
import com.bj.convo.global.util.redis.RedisUtil;
import jakarta.mail.MessagingException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    public void register(RegisterRequest req) {
        if (usersRepository.existsByEmail(req.email())) {
            throw new RestApiException(UsersErrorCode.ALREADY_EXIST_EMAIL);
        }

        if (!req.isVerified()) {
            throw new RestApiException(UsersErrorCode.NOT_VERIFIED_EMAIL);
        }

        String encryptedPassword = passwordEncoder.encode(req.password());
        Users entity = req.toEntity(encryptedPassword);
        usersRepository.save(entity);
    }

    public void sendCodeToEmail(String email) {
        if (usersRepository.existsByEmail(email)) {
            throw new RestApiException(UsersErrorCode.ALREADY_EXIST_EMAIL);
        }

        String verifyCode = createVerifyCode(email);

        String title = "[Convo] 요청하신 인증코드는 " + verifyCode + "입니다.";

        String content = "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f8f8f8; text-align: center; padding: 50px;'>"
                + "<div style='background-color: white; width: 400px; margin: auto; padding: 30px; border-radius: 10px; "
                + "box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>"
                + "<h1 style='font-size: 24px; color: #333;'>인증코드를 확인하세요.</h1>"
                + "<p style='font-size: 16px; color: #555;'>아래 인증코드를 진행 중인 화면에 입력하여 인증을 완료해 주세요.</p>"
                + "<div style='font-size: 32px; font-weight: bold; color: #007bff; margin: 20px 0;'>" + verifyCode
                + "</div>"
                + "<p style='font-size: 14px; color: #666;'>• 인증코드는 발송된 시점부터 10분 동안 유효합니다.<br>"
                + "• 유효시간 내 인증을 완료하지 않을 경우 재요청이 필요합니다.</p>"
                + "<footer style='margin-top: 20px; font-size: 12px; color: gray;'>"
                + "Copyright © 2025 Convo. All rights reserved."
                + "</footer>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmail(email, title, content);
        } catch (RuntimeException | MessagingException e) {
            throw new RestApiException(UsersErrorCode.SEND_EMAIL_FAILED);
        }
    }

    public void verifyCode(VerifyEmailRequest req) {
        if (!req.verifyCode().equals(redisUtil.getData("email:" + req.email()))) {
            throw new RestApiException(UsersErrorCode.INCORRECT_VERIFY_CODE);
        }
    }

    private String createVerifyCode(String email) {
        String redisKey = "email:" + email;
        String verifyCode = generateRandomCode();

        Long authCodeExpiration = 600000L;
        redisUtil.setData(redisKey, verifyCode, authCodeExpiration);
        return verifyCode;
    }

    private String generateRandomCode() {
        // 숫자 + 대문자 + 소문자
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
