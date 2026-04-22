package com.hyperpass.controller;

import com.hyperpass.domain.VisitHistory;
import com.hyperpass.domain.WaitingQueue;
import com.hyperpass.dto.QueueStatusUpdateRequest;
import com.hyperpass.dto.VisitRequest;
import com.hyperpass.dto.VisitResponse;
import com.hyperpass.service.VisitService;
import com.hyperpass.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;
    private final WaitingQueueService queueService;

    /** 접수 처리: 초진/재진 판별 → 대기 순번 발급 → 이벤트 발행 */
    @PostMapping("/visits")
    public ResponseEntity<VisitResponse> processVisit(
            @RequestBody VisitRequest request,
            Authentication authentication) {
        Long patientId = Long.valueOf(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitService.process(patientId, request));
    }

    /** 환자 내원 이력 조회 */
    @GetMapping("/visits/patient/{patientId}")
    public ResponseEntity<List<VisitHistory>> getVisitHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(visitService.getHistory(patientId));
    }

    /** 진료과별 현재 대기 목록 조회 */
    @GetMapping("/queue/department/{departmentId}")
    public ResponseEntity<List<WaitingQueue>> getQueueByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "WAITING") String status) {
        return ResponseEntity.ok(queueService.findByDepartmentAndStatus(departmentId, status));
    }

    /** 대기 상태 변경 (CALLED / DONE / CANCELLED) */
    @PatchMapping("/queue/{queueId}/status")
    public ResponseEntity<WaitingQueue> updateQueueStatus(
            @PathVariable Long queueId,
            @RequestBody QueueStatusUpdateRequest request) {
        return ResponseEntity.ok(queueService.updateStatus(queueId, request.getStatus()));
    }
}
