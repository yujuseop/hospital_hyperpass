package com.hyperpass.service;

import com.hyperpass.domain.WaitingQueue;
import com.hyperpass.event.QueueUpdatedEvent;
import com.hyperpass.mapper.WaitingQueueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingQueueServiceImpl implements WaitingQueueService {

    private final WaitingQueueMapper queueMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public WaitingQueue issue(Long patientId, Long departmentId) {
        // Phase 6에서 Redis INCR으로 교체 (현재는 당일 최대값 + 1)
        int nextNumber = queueMapper.maxQueueNumberByDepartmentId(departmentId) + 1;

        WaitingQueue queue = new WaitingQueue();
        queue.setPatientId(patientId);
        queue.setDepartmentId(departmentId);
        queue.setQueueNumber(nextNumber);
        queue.setStatus("WAITING");
        queue.setQueuedAt(LocalDateTime.now());
        queueMapper.insert(queue);

        eventPublisher.publishEvent(new QueueUpdatedEvent(
                queue.getId(), patientId, departmentId, nextNumber, "WAITING"
        ));

        return queue;
    }

    @Override
    public WaitingQueue updateStatus(Long queueId, String status) {
        LocalDateTime calledAt    = "CALLED".equals(status)                          ? LocalDateTime.now() : null;
        LocalDateTime completedAt = ("DONE".equals(status) || "CANCELLED".equals(status)) ? LocalDateTime.now() : null;

        queueMapper.updateStatus(queueId, status, calledAt, completedAt);
        WaitingQueue updated = queueMapper.findById(queueId);

        eventPublisher.publishEvent(new QueueUpdatedEvent(
                updated.getId(), updated.getPatientId(), updated.getDepartmentId(),
                updated.getQueueNumber(), status
        ));

        return updated;
    }

    @Override
    public List<WaitingQueue> findByDepartmentAndStatus(Long departmentId, String status) {
        return queueMapper.findByDepartmentIdAndStatus(departmentId, status);
    }
}
