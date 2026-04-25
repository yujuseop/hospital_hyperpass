package com.hyperpass.service;

import com.hyperpass.dto.StaffLoginRequest;
import com.hyperpass.dto.StaffLoginResponse;

public interface StaffAuthService {

    StaffLoginResponse login(StaffLoginRequest request);
}
