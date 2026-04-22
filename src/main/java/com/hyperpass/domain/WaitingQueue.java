package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class WaitingQueue {

    private Long id;
    private Long patientId;
    private Long departmentId;
    private int queueNumber;
    private String status;      // WAITING | CALLED | DONE | CANCELLED
    private LocalDateTime queuedAt;
    private LocalDateTime calledAt;
    private LocalDateTime completedAt;
}
