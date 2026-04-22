package com.hyperpass.service;

import com.hyperpass.domain.HospitalPatientMapping;
import com.hyperpass.domain.Patient;
import com.hyperpass.dto.IdentifyResult;
import com.hyperpass.dto.VisitRequest;
import com.hyperpass.exception.PatientNotFoundException;
import com.hyperpass.mapper.HospitalPatientMappingMapper;
import com.hyperpass.mapper.PatientMapper;
import com.hyperpass.mapper.VisitHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientIdentifyServiceImpl implements PatientIdentifyService {

    private final PatientMapper patientMapper;
    private final VisitHistoryMapper visitHistoryMapper;
    private final HospitalPatientMappingMapper mappingMapper;

    @Override
    public IdentifyResult identify(Long patientId, VisitRequest request) {
        Patient patient = patientMapper.findById(patientId);
        if (patient == null) {
            throw new PatientNotFoundException(patientId);
        }

        // 이전 내원 이력 수로 초진/재진 판별
        int visitCount = visitHistoryMapper.countByPatientId(patientId);
        String visitType = (visitCount == 0) ? "FIRST" : "RETURN";

        // HIS 환자번호가 전달된 경우 매핑 등록 (없으면 skip)
        if (request.getHisPatientNo() != null) {
            HospitalPatientMapping existing = mappingMapper.findByPatientIdAndHospitalCode(
                    patientId, request.getHospitalCode());
            if (existing == null) {
                HospitalPatientMapping mapping = new HospitalPatientMapping();
                mapping.setPatientId(patientId);
                mapping.setHisPatientNo(request.getHisPatientNo());
                mapping.setHospitalCode(request.getHospitalCode());
                mappingMapper.insert(mapping);
            }
        }

        return new IdentifyResult(patient, visitType);
    }
}
