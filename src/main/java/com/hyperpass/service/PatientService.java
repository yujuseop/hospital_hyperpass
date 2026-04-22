package com.hyperpass.service;

import com.hyperpass.dto.PatientRegisterRequest;
import com.hyperpass.dto.PatientResponse;

public interface PatientService {

    PatientResponse register(PatientRegisterRequest request);

    PatientResponse findById(Long id);

    PatientResponse findByCiValue(String ciValue);
}
