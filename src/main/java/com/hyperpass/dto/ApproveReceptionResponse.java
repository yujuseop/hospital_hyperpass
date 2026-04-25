package com.hyperpass.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApproveReceptionResponse {

    private Long receptionId;
    private Long departmentId;
    private String status;
    private LocalDateTime approvedAt;
    private String message;
}
