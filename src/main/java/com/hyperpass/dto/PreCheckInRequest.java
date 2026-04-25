package com.hyperpass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PreCheckInRequest {

    private String mainSymptom;
    private List<String> symptomKeywords;
    private String painArea;
    private Integer painLevel;
    private String startedAtText;
    private String freeText;
}
