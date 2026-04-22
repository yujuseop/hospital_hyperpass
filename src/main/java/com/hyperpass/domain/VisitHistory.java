package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class VisitHistory {

    private Long id;
    private Long patientId;
    private Long kioskId;
    private Long departmentId;
    private String visitType;   // FIRST | RETURN
    private String status;
    private LocalDateTime visitedAt;
}
