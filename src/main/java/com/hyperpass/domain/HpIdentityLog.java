package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class HpIdentityLog {
    private Long id;
    private Long receptionId;
    private Long staffId;
    private LocalDateTime verifiedAt;
}
