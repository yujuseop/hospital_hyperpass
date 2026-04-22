package com.hyperpass.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("환자를 찾을 수 없습니다. id=" + id);
    }

    public PatientNotFoundException(String identifier) {
        super("환자를 찾을 수 없습니다. " + identifier);
    }
}
