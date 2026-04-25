package com.hyperpass.service;

import com.hyperpass.dto.ApproveReceptionResponse;
import com.hyperpass.dto.PendingReceptionResponse;
import com.hyperpass.dto.PreCheckInRequest;
import com.hyperpass.dto.PreCheckInResponse;

import java.util.List;

public interface HpReceptionService {
    PreCheckInResponse preCheckIn(Long patientId, PreCheckInRequest request);
    List<PendingReceptionResponse> getPending();
    void verifyId(Long receptionId, Long staffId);
    ApproveReceptionResponse approve(Long receptionId, Long staffId, Long departmentId);
}
