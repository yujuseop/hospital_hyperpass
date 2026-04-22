package com.hyperpass.service;

import com.hyperpass.domain.Patient;
import com.hyperpass.dto.PatientRegisterRequest;
import com.hyperpass.dto.PatientResponse;
import com.hyperpass.exception.PatientNotFoundException;
import com.hyperpass.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientMapper patientMapper;

    @Override
    public PatientResponse register(PatientRegisterRequest request) {
        if (patientMapper.countByCiValue(request.getCiValue()) > 0) {
            Patient existing = patientMapper.findByCiValue(request.getCiValue());
            existing.setLastVisitAt(LocalDateTime.now());
            patientMapper.update(existing);
            return toResponse(existing);
        }

        LocalDateTime now = LocalDateTime.now();
        Patient patient = new Patient();
        patient.setCiValue(request.getCiValue());
        patient.setName(request.getName());
        patient.setBirthDate(request.getBirthDate());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setFirstVisitAt(now);
        patient.setLastVisitAt(now);
        patientMapper.insert(patient);

        return toResponse(patient);
    }

    @Override
    public PatientResponse findById(Long id) {
        Patient patient = patientMapper.findById(id);
        if (patient == null) {
            throw new PatientNotFoundException(id);
        }
        return toResponse(patient);
    }

    @Override
    public PatientResponse findByCiValue(String ciValue) {
        Patient patient = patientMapper.findByCiValue(ciValue);
        if (patient == null) {
            throw new PatientNotFoundException("CI: " + ciValue);
        }
        return toResponse(patient);
    }

    private PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .ciValue(patient.getCiValue())
                .name(patient.getName())
                .birthDate(patient.getBirthDate())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .firstVisitAt(patient.getFirstVisitAt())
                .lastVisitAt(patient.getLastVisitAt())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
