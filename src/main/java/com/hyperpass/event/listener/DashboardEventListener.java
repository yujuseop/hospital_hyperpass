package com.hyperpass.event.listener;

import com.hyperpass.event.PatientRegisteredEvent;
import com.hyperpass.event.QueueUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DashboardEventListener {

    // Phase 5에서 WebSocket STOMP 브로드캐스트로 교체
    @Async
    @EventListener
    public void onPatientRegistered(PatientRegisteredEvent event) {
        log.info("[대시보드] 신규 접수 — patientId={}, visitType={}",
                event.patientId(), event.visitType());
    }

    @Async
    @EventListener
    public void onQueueUpdated(QueueUpdatedEvent event) {
        log.info("[대시보드] 대기 갱신 — deptId={}, queueId={}, status={}",
                event.departmentId(), event.queueId(), event.status());
    }
}
