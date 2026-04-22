package com.hyperpass.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VisitResponse {

    private Long visitId;
    private Long patientId;
    private String visitType;       // FIRST | RETURN
    private int queueNumber;
    private Long departmentId;
    private LocalDateTime visitedAt;
}
