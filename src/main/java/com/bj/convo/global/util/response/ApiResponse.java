package com.bj.convo.global.util.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

    public static ResponseEntity<Void> OK() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public static <T> ResponseEntity<T> OK(T data) {
        return ResponseEntity.ok(data);
    }

    public static ResponseEntity<Void> CREATED() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public static <T> ResponseEntity<T> CREATED(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    public static <T> ResponseEntity<T> NO_CONTENT() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
