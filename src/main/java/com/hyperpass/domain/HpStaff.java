package com.hyperpass.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class HpStaff {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private LocalDateTime createdAt;
}
