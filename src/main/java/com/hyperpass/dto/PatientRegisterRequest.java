package com.hyperpass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PatientRegisterRequest {

    private String ciValue;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String phone;
}
