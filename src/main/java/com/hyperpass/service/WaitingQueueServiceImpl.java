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
    private final NotificationService notificationService;

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
        LocalDateTime calledAt    = "CALLED".equals(status) ? LocalDateTime.now() : null;
        LocalDateTime completedAt = ("DONE".equals(status) || "CANCELLED".equals(status)) ? LocalDateTime.now() : null;

        queueMapper.updateStatus(queueId, status, calledAt, completedAt);
        WaitingQueue updated = queueMapper.findById(queueId);

        eventPublisher.publishEvent(new QueueUpdatedEvent(
                updated.getId(), updated.getPatientId(), updated.getDepartmentId(),
                updated.getQueueNumber(), status
        ));

        // 호출·완료 시 남은 대기 3번째 환자에게 재알림 발송
        if ("CALLED".equals(status) || "DONE".equals(status)) {
            triggerReminderIfNeeded(updated.getDepartmentId());
        }

        return updated;
    }

    @Override
    public List<WaitingQueue> findByDepartmentAndStatus(Long departmentId, String status) {
        return queueMapper.findByDepartmentIdAndStatus(departmentId, status);
    }

    private void triggerReminderIfNeeded(Long departmentId) {
        List<WaitingQueue> waiting = queueMapper.findByDepartmentIdAndStatus(departmentId, "WAITING");
        // 남은 대기 목록의 3번째 환자 (앞에 2명) 에게 알림
        if (waiting.size() >= 3) {
            WaitingQueue thirdInLine = waiting.get(2);
            notificationService.sendReminder(thirdInLine.getPatientId(), 2);
        }
    }
}
