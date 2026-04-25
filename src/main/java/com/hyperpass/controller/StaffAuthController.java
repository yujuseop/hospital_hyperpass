package com.hyperpass.controller;

import com.hyperpass.dto.StaffLoginRequest;
import com.hyperpass.dto.StaffLoginResponse;
import com.hyperpass.service.StaffAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class StaffAuthController {

    private final StaffAuthService staffAuthService;

    @PostMapping("/login")
    public ResponseEntity<StaffLoginResponse> login(@RequestBody StaffLoginRequest request) {
        return ResponseEntity.ok(staffAuthService.login(request));
    }
}
