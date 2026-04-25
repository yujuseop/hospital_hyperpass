package com.hyperpass.exception;

// 동일 환자가 동시에 여러 접수 흐름을 만들지 못하게 막는 예외.
public class ActiveReceptionExistsException extends RuntimeException {

    public ActiveReceptionExistsException(Long patientId) {
        super("이미 진행 중인 접수 건이 존재합니다. patientId=" + patientId);
    }
}
