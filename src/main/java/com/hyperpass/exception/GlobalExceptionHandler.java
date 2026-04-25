package com.hyperpass.exception;

import com.hyperpass.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .code("VALIDATION_ERROR")
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 원무과 승인 단계에서 발생하는 상태/입력 오류를 사용자에게 명확히 전달한다.
    @ExceptionHandler(ReceptionApprovalException.class)
    public ResponseEntity<ErrorResponse> handleReceptionApproval(ReceptionApprovalException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .code("RECEPTION_APPROVAL_ERROR")
                        .message(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 환자가 이미 진행 중인 접수 건을 가진 상태에서 다시 제출하는 것을 막는다.
    @ExceptionHandler(ActiveReceptionExistsException.class)
    public ResponseEntity<ErrorResponse> handleActiveReceptionExists(ActiveReceptionExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(409)
                        .code("ACTIVE_RECEPTION_EXISTS")
                        .message(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 인증은 되었지만 환자 엔티티가 없는 비정상 상황을 처리한다.
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(PatientNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(404)
                        .code("PATIENT_NOT_FOUND")
                        .message(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 예기치 못한 서버 예외는 민감 정보 없이 공통 오류로 감싼다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(500)
                        .code("INTERNAL_ERROR")
                        .message("서버 오류가 발생했습니다.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
