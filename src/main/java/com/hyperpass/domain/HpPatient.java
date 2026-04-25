package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class HpPatient {
    private Long id;
    private String name;
    private String encRrn;
    private String address;
    private String phone;
    private LocalDate lastVisitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
