package com.hyperpass.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PreCheckInResponse {

    private Long receptionId;
    private String visitType;
    private String status;
    private String message;
}
