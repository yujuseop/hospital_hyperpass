package com.hyperpass.event;

public record DepartmentMatchedEvent(Long patientId, Long departmentId, String keyword) {}
