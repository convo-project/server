package com.bj.convo.global.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendEmail(String toEmail, String title, String content) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(title);
        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setReplyTo("convoauthentication@gmail.com"); // 회신 불가능 이메일 주소
        try {
            emailSender.send(mimeMessage);
        } catch (RuntimeException e) {
            log.error("Send failed: ", e);
        }
    }
}
