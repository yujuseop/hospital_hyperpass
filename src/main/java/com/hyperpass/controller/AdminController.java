package com.hyperpass.controller;

import com.hyperpass.dto.ApproveReceptionRequest;
import com.hyperpass.dto.ApproveReceptionResponse;
import com.hyperpass.dto.PendingReceptionResponse;
import com.hyperpass.service.HpReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HpReceptionService hpReceptionService;

    @GetMapping("/receptions/pending")
    public ResponseEntity<List<PendingReceptionResponse>> getPending() {
        return ResponseEntity.ok(hpReceptionService.getPending());
    }

    @PatchMapping("/receptions/{id}/verify-id")
    public ResponseEntity<Void> verifyId(@PathVariable Long id, Authentication authentication) {
        Long staffId = Long.valueOf(authentication.getName());
        hpReceptionService.verifyId(id, staffId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/receptions/{id}/approve")
    public ResponseEntity<ApproveReceptionResponse> approve(
            @PathVariable Long id,
            @RequestBody ApproveReceptionRequest request,
            Authentication authentication) {
        Long staffId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(hpReceptionService.approve(id, staffId, request.getDepartmentId()));
    }
}
