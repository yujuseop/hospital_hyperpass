package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class HpReception {
    private Long id;
    private Long patientId;
    private Long departmentId;
    private Long approvedBy;
    private String visitType;
    private String status;
    private Boolean idVerified;
    private LocalDateTime idVerifiedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
