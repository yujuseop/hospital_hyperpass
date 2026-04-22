package com.hyperpass.controller;

import com.hyperpass.dto.AuthRequest;
import com.hyperpass.dto.AuthResponse;
import com.hyperpass.dto.PatientResponse;
import com.hyperpass.service.PatientService;
import com.hyperpass.util.JwtUtil;
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

    private final PatientService patientService;
    private final JwtUtil jwtUtil;

    /**
     * 카카오 인증서 CI 검증 후 JWT 발급.
     * Phase 3에서 실제 카카오 SDK 콜백과 연결.
     */
    @PostMapping("/verify-kakao")
    public ResponseEntity<AuthResponse> verifyKakao(@RequestBody AuthRequest request) {
        PatientResponse patient = patientService.findOrCreateForAuth(request);

        String token = jwtUtil.generate(patient.getId(), "PATIENT");

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600)
                .build());
    }
}
