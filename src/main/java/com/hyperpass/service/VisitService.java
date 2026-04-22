package com.hyperpass.service;

import com.hyperpass.domain.VisitHistory;
import com.hyperpass.dto.VisitRequest;
import com.hyperpass.dto.VisitResponse;

import java.util.List;

public interface VisitService {

    VisitResponse process(Long patientId, VisitRequest request);

    List<VisitHistory> getHistory(Long patientId);
}
