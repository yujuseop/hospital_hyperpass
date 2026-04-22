package com.hyperpass.event;

public record QueueUpdatedEvent(
        Long queueId,
        Long patientId,
        Long departmentId,
        int queueNumber,
        String status
) {}
