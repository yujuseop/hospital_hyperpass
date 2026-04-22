package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Patient {

    private Long id;
    private String ciValue;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String phone;
    private String encSsn;
    private LocalDateTime firstVisitAt;
    private LocalDateTime lastVisitAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
