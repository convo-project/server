package com.bj.convo.domain.user.controller;

import com.bj.convo.domain.user.model.dto.request.RegisterRequest;
import com.bj.convo.domain.user.service.UsersService;
import com.bj.convo.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UsersController {

    private final UsersService usersService;
// TODO: 테스트 코드
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ApiResponse.NO_CONTENT();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        usersService.register(req);
        return ApiResponse.CREATED();
    }

}
