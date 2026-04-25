package com.hyperpass.service;

import com.hyperpass.domain.HpPatient;
import com.hyperpass.dto.AuthRequest;
import com.hyperpass.exception.PatientNotFoundException;
import com.hyperpass.mapper.HpPatientMapper;
import com.hyperpass.util.AesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HpPatientServiceImpl implements HpPatientService {

    private final HpPatientMapper hpPatientMapper;
    private final AesUtil aesUtil;

    @Override
    @Transactional
    public HpPatient findOrCreate(AuthRequest request) {
        HpPatient patient = hpPatientMapper.findByNameAndPhone(request.getName(), request.getPhone());
        String encRrn = (request.getRrn() != null && !request.getRrn().isBlank())
                ? aesUtil.encrypt(request.getRrn())
                : null;

        if (patient == null) {
            patient = new HpPatient();
            patient.setName(request.getName());
            patient.setEncRrn(encRrn);
            patient.setAddress(request.getAddress());
            patient.setPhone(request.getPhone());
            hpPatientMapper.insert(patient);
        } else {
            patient.setEncRrn(encRrn);
            patient.setAddress(request.getAddress());
            patient.setPhone(request.getPhone());
            hpPatientMapper.updateAuthProfile(patient);
        }
        return patient;
    }

    @Override
    public HpPatient findById(Long id) {
        HpPatient patient = hpPatientMapper.findById(id);
        if (patient == null) {
            throw new PatientNotFoundException(id);
        }
        return patient;
    }
}
