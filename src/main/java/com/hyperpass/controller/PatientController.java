package com.hyperpass.controller;

import com.hyperpass.dto.PatientRegisterRequest;
import com.hyperpass.dto.PatientResponse;
import com.hyperpass.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> register(@RequestBody PatientRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.register(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @GetMapping("/ci/{ciValue}")
    public ResponseEntity<PatientResponse> findByCiValue(@PathVariable String ciValue) {
        return ResponseEntity.ok(patientService.findByCiValue(ciValue));
    }
}
