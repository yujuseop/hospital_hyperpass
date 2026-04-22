package com.hyperpass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QueueStatusUpdateRequest {

    private String status;  // CALLED | DONE | CANCELLED
}
