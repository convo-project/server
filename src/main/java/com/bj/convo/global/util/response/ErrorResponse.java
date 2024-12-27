package com.bj.convo.global.util.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final String code;
    private final String message;
//
//    @JsonInclude(Include.NON_EMPTY)
//    private final List<ValidationError> errors;
//
//    @Getter
//    @Builder
//    @RequiredArgsConstructor
//    public static class ValidationError {
//
//        private final String field;
//        private final String message;
//
//        public static ValidationError of(final FieldError fieldError) {
//            return ValidationError.builder()
//                    .field(fieldError.getField())
//                    .message(fieldError.getDefaultMessage())
//                    .build();
//        }
//    }
}
