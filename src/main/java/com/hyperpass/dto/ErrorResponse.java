package com.hyperpass.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private int status;
    private String code;
    private String message;
    private LocalDateTime timestamp;
}
