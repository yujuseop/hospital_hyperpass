package com.hyperpass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PendingReceptionResponse {

    private Long receptionId;
    private Long patientId;
    private String patientName;
    private String visitType;
    private Boolean isIdVerified;
    private Long departmentId;
    private String departmentName;
    private String mainSymptom;
    private String symptomKeywords;
    private String painArea;
    private Integer painLevel;
    private String freeText;
    private LocalDateTime submittedAt;
}
