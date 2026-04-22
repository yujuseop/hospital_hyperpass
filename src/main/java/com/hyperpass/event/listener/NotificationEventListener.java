package com.hyperpass.event.listener;

import com.hyperpass.event.PatientRegisteredEvent;
import com.hyperpass.event.QueueUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventListener {

    // Phase 4에서 실제 알림톡 API(SMS/카카오 비즈메시지)로 교체
    @Async
    @EventListener
    public void onPatientRegistered(PatientRegisteredEvent event) {
        log.info("[알림톡] 접수 완료 — patientId={}, visitType={}",
                event.patientId(), event.visitType());
    }

    @Async
    @EventListener
    public void onQueueUpdated(QueueUpdatedEvent event) {
        log.info("[알림톡] 대기 상태 변경 — patientId={}, 순번={}, status={}",
                event.patientId(), event.queueNumber(), event.status());
    }
}
