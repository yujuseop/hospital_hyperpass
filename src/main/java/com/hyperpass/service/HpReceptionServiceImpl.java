package com.hyperpass.service;

import com.hyperpass.domain.HpIdentityLog;
import com.hyperpass.domain.HpPatient;
import com.hyperpass.domain.HpReception;
import com.hyperpass.domain.HpTriageRecord;
import com.hyperpass.dto.ApproveReceptionResponse;
import com.hyperpass.dto.PendingReceptionResponse;
import com.hyperpass.dto.PreCheckInRequest;
import com.hyperpass.dto.PreCheckInResponse;
import com.hyperpass.exception.ActiveReceptionExistsException;
import com.hyperpass.exception.PatientNotFoundException;
import com.hyperpass.exception.ReceptionApprovalException;
import com.hyperpass.mapper.HpIdentityLogMapper;
import com.hyperpass.mapper.HpPatientMapper;
import com.hyperpass.mapper.HpReceptionMapper;
import com.hyperpass.mapper.HpTriageRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HpReceptionServiceImpl implements HpReceptionService {

    private final HpPatientMapper hpPatientMapper;
    private final HpReceptionMapper hpReceptionMapper;
    private final HpTriageRecordMapper hpTriageRecordMapper;
    private final HpIdentityLogMapper hpIdentityLogMapper;

    @Override
    @Transactional
    public PreCheckInResponse preCheckIn(Long patientId, PreCheckInRequest request) {
        HpPatient patient = hpPatientMapper.findById(patientId);
        if (patient == null) {
            throw new PatientNotFoundException(patientId);
        }

        if (hpReceptionMapper.findActiveByPatientId(patientId) != null) {
            throw new ActiveReceptionExistsException(patientId);
        }

        // 6개월 이내 방문 이력이 있으면 재진, 없으면 초진
        String visitType = isReturnVisit(patient) ? "RETURN" : "FIRST";

        LocalDateTime now = LocalDateTime.now();
        HpReception reception = new HpReception();
        reception.setPatientId(patientId);
        reception.setVisitType(visitType);
        reception.setStatus("SUBMITTED");
        reception.setIdVerified(false);
        reception.setCreatedAt(now);
        reception.setUpdatedAt(now);
        hpReceptionMapper.insert(reception);

        HpTriageRecord triage = new HpTriageRecord();
        triage.setReceptionId(reception.getId());
        triage.setPatientId(patientId);
        triage.setMainSymptom(resolveMainSymptom(request));
        triage.setSymptomKeywords(joinKeywords(request.getSymptomKeywords()));
        triage.setPainArea(request.getPainArea());
        triage.setPainLevel(request.getPainLevel());
        triage.setStartedAtText(request.getStartedAtText());
        triage.setFreeText(request.getFreeText());
        triage.setCreatedAt(now);
        hpTriageRecordMapper.insert(triage);

        return PreCheckInResponse.builder()
                .receptionId(reception.getId())
                .visitType(visitType)
                .status("SUBMITTED")
                .message("사전 문진이 제출되었습니다. 직원 확인 후 접수가 완료됩니다.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingReceptionResponse> getPending() {
        return hpReceptionMapper.findPending();
    }

    @Override
    @Transactional
    public void verifyId(Long receptionId, Long staffId) {
        HpReception reception = hpReceptionMapper.findById(receptionId);
        if (reception == null || !"SUBMITTED".equals(reception.getStatus())) {
            throw new ReceptionApprovalException("신분증 확인 처리할 수 없는 접수 건입니다. receptionId=" + receptionId);
        }

        LocalDateTime now = LocalDateTime.now();
        hpReceptionMapper.updateIdVerified(receptionId, now);

        HpIdentityLog log = new HpIdentityLog();
        log.setReceptionId(receptionId);
        log.setStaffId(staffId);
        log.setVerifiedAt(now);
        hpIdentityLogMapper.insert(log);
    }

    @Override
    @Transactional
    public ApproveReceptionResponse approve(Long receptionId, Long staffId, Long departmentId) {
        HpReception reception = hpReceptionMapper.findById(receptionId);
        if (reception == null) {
            throw new ReceptionApprovalException("접수 건을 찾을 수 없습니다. receptionId=" + receptionId);
        }
        if (!"SUBMITTED".equals(reception.getStatus())) {
            throw new ReceptionApprovalException("승인 가능한 상태가 아닙니다. 현재 상태=" + reception.getStatus());
        }
        // 초진은 신분증 확인 완료 후에만 승인 가능
        if ("FIRST".equals(reception.getVisitType()) && !Boolean.TRUE.equals(reception.getIdVerified())) {
            throw new ReceptionApprovalException("초진 환자는 신분증 확인 완료 후 승인할 수 있습니다.");
        }
        if (departmentId == null) {
            throw new ReceptionApprovalException("진료과를 선택해야 합니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        hpReceptionMapper.updateApproval(receptionId, departmentId, staffId, now, now);

        // 승인 시점을 last_visit_date 로 기록해 다음 방문 시 재진 판별에 사용
        hpPatientMapper.updateLastVisitDate(reception.getPatientId(), now.toLocalDate());

        return ApproveReceptionResponse.builder()
                .receptionId(receptionId)
                .departmentId(departmentId)
                .status("APPROVED")
                .approvedAt(now)
                .message("접수가 승인되었습니다.")
                .build();
    }

    private boolean isReturnVisit(HpPatient patient) {
        if (patient.getLastVisitDate() == null) return false;
        return patient.getLastVisitDate().isAfter(LocalDate.now().minusMonths(6));
    }

    private String resolveMainSymptom(PreCheckInRequest request) {
        if (request.getMainSymptom() != null && !request.getMainSymptom().isBlank()) {
            return request.getMainSymptom();
        }
        List<String> keywords = request.getSymptomKeywords();
        return (keywords != null && !keywords.isEmpty()) ? keywords.get(0) : null;
    }

    private String joinKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return null;
        return String.join(",", keywords);
    }
}
