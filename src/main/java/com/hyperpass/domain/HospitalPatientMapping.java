package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HospitalPatientMapping {

    private Long id;
    private Long patientId;
    private String hisPatientNo;
    private String hospitalCode;
    private LocalDateTime createdAt;
}
