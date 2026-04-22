package com.hyperpass.service;

import com.hyperpass.domain.VisitHistory;
import com.hyperpass.domain.WaitingQueue;
import com.hyperpass.dto.IdentifyResult;
import com.hyperpass.dto.VisitRequest;
import com.hyperpass.dto.VisitResponse;
import com.hyperpass.event.DepartmentMatchedEvent;
import com.hyperpass.event.PatientRegisteredEvent;
import com.hyperpass.mapper.VisitHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final PatientIdentifyService identifyService;
    private final WaitingQueueService queueService;
    private final VisitHistoryMapper visitHistoryMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public VisitResponse process(Long patientId, VisitRequest request) {
        // 1. 초진/재진 판별 + HIS 매핑
        IdentifyResult result = identifyService.identify(patientId, request);

        // 2. 내원 이력 기록
        VisitHistory history = new VisitHistory();
        history.setPatientId(result.patient().getId());
        history.setKioskId(request.getKioskId());
        history.setDepartmentId(request.getDepartmentId());
        history.setVisitType(result.visitType());
        history.setStatus("REGISTERED");
        history.setVisitedAt(LocalDateTime.now());
        visitHistoryMapper.insert(history);

        // 3. 대기 순번 발급
        WaitingQueue queue = queueService.issue(result.patient().getId(), request.getDepartmentId());

        // 4. 비동기 이벤트 발행 (트랜잭션 커밋 이후 리스너 동작)
        eventPublisher.publishEvent(
                new PatientRegisteredEvent(result.patient().getId(), result.visitType())
        );
        if (request.getSymptomKeyword() != null) {
            eventPublisher.publishEvent(
                    new DepartmentMatchedEvent(
                            result.patient().getId(),
                            request.getDepartmentId(),
                            request.getSymptomKeyword())
            );
        }

        return VisitResponse.builder()
                .visitId(history.getId())
                .patientId(result.patient().getId())
                .visitType(result.visitType())
                .queueNumber(queue.getQueueNumber())
                .departmentId(request.getDepartmentId())
                .visitedAt(history.getVisitedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitHistory> getHistory(Long patientId) {
        return visitHistoryMapper.findByPatientId(patientId);
    }
}
