package com.hyperpass.event.listener;

import com.hyperpass.event.PatientRegisteredEvent;
import com.hyperpass.event.QueueUpdatedEvent;
import com.hyperpass.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onPatientRegistered(PatientRegisteredEvent event) {
        notificationService.sendRegistered(event.patientId(), event.visitType());
    }

    @Async
    @EventListener
    public void onQueueUpdated(QueueUpdatedEvent event) {
        if ("CALLED".equals(event.status())) {
            notificationService.sendCalled(
                    event.patientId(), event.queueNumber(), event.departmentId());
        }
    }
}
