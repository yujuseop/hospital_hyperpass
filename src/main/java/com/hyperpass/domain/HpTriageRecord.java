package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class HpTriageRecord {
    private Long id;
    private Long receptionId;
    private Long patientId;
    private String mainSymptom;
    private String symptomKeywords;
    private String painArea;
    private Integer painLevel;
    private String startedAtText;
    private String freeText;
    private LocalDateTime createdAt;
}
