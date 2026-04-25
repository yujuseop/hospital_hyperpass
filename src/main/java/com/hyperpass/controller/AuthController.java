package com.hyperpass.controller;

import com.hyperpass.domain.HpPatient;
import com.hyperpass.dto.AuthRequest;
import com.hyperpass.dto.AuthResponse;
import com.hyperpass.service.HpPatientService;
import com.hyperpass.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final HpPatientService hpPatientService;
    private final JwtUtil jwtUtil;

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verify(@Valid @RequestBody AuthRequest request) {
        HpPatient patient = hpPatientService.findOrCreate(request);

        String token = jwtUtil.generate(patient.getId(), "PATIENT");

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600)
                .build());
    }
}
