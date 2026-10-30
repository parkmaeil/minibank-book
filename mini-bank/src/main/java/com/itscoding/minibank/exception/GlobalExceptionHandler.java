package com.itscoding.minibank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice // 애플리케이션 전역에서 발생하는 예외를 낚아채서 처리합니다.
public class GlobalExceptionHandler {

    // 우리가 만든 AccountNotFoundException이 발생하면 이 메서드가 실행됩니다.
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 상태 코드
                .body(Map.of("message", e.getMessage()));
    }

    // @Valid 검증에 실패하면 이 예외가 발생합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException e) {

        // 발생한 에러 중 첫 번째 에러 메시지를 꺼내옵니다.
        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        return ResponseEntity.badRequest() // 400 상태 코드
                .body(Map.of("message", message));
    }
}
