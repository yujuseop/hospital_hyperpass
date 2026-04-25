package com.hyperpass.controller;

import com.hyperpass.dto.PreCheckInRequest;
import com.hyperpass.dto.PreCheckInResponse;
import com.hyperpass.service.HpReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receptions")
@RequiredArgsConstructor
public class ReceptionController {

    private final HpReceptionService hpReceptionService;

    @PostMapping("/precheckin")
    public ResponseEntity<PreCheckInResponse> preCheckIn(
            @RequestBody PreCheckInRequest request,
            Authentication authentication) {
        Long patientId = Long.valueOf(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hpReceptionService.preCheckIn(patientId, request));
    }
}
