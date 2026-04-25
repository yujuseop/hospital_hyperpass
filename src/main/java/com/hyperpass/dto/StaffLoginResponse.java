package com.hyperpass.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffLoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String name;
}
