package com.bj.convo.domain.user.controller;

import com.bj.convo.domain.user.model.dto.request.RegisterRequest;
import com.bj.convo.domain.user.model.dto.request.VerifyEmailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "사용자")
public interface SwaggerUsersController {
    @Operation(summary = "인가 테스트", description = "API 접근이 정상적으로 되는지 테스트 하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "접근 성공"),
            @ApiResponse(responseCode = "401", description = "접근 실패")
    })
    ResponseEntity<?> test();

    @Operation(summary = "회원가입", description = "OAuth가 아닌 일반 사용자 가입하는 API")
    ResponseEntity<?> register(@RequestBody RegisterRequest req);

    @Operation(summary = "이메일 인증코드 전송", description = "이메일 중복 확인 및 인증 코드를 이메일로 전송 하는 API")
    ResponseEntity<?> sendEmail(@RequestParam String email);

    @Operation(summary = "이메일 인증코드 검증", description = "전송된 인증 코드를 검증 하는 API")
    ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest req);
}
