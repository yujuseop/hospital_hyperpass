package com.hyperpass.dto;

import com.hyperpass.domain.Patient;

public record IdentifyResult(Patient patient, String visitType) {}
