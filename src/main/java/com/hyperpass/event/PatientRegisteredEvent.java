package com.hyperpass.event;

public record PatientRegisteredEvent(Long patientId, String visitType) {}
