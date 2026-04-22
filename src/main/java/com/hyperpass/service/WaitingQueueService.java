package com.hyperpass.service;

import com.hyperpass.domain.WaitingQueue;

import java.util.List;

public interface WaitingQueueService {

    WaitingQueue issue(Long patientId, Long departmentId);

    WaitingQueue updateStatus(Long queueId, String status);

    List<WaitingQueue> findByDepartmentAndStatus(Long departmentId, String status);
}
