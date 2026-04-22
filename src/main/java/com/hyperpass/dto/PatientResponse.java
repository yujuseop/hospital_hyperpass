package com.hyperpass.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PatientResponse {

    private Long id;
    private String ciValue;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String phone;
    private LocalDateTime firstVisitAt;
    private LocalDateTime lastVisitAt;
    private LocalDateTime createdAt;
}
