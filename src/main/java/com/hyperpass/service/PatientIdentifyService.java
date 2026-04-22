package com.hyperpass.service;

import com.hyperpass.dto.IdentifyResult;
import com.hyperpass.dto.VisitRequest;

public interface PatientIdentifyService {

    IdentifyResult identify(Long patientId, VisitRequest request);
}
