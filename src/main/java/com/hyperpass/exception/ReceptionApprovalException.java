package com.hyperpass.exception;

// 원무과 승인 단계에서 상태 불일치나 필수 정보 누락이 발생했을 때 사용하는 예외.
public class ReceptionApprovalException extends RuntimeException {

    public ReceptionApprovalException(String message) {
        super(message);
    }
}
