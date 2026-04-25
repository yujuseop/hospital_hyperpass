package com.hyperpass.service;

import com.hyperpass.domain.HpPatient;
import com.hyperpass.dto.AuthRequest;

public interface HpPatientService {
    HpPatient findOrCreate(AuthRequest request);
    HpPatient findById(Long id);
}
