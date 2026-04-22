package com.hyperpass.event.listener;

import com.hyperpass.event.DepartmentMatchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DepartmentMatchEventListener {

    // Phase 4에서 symptom_department_mapping 조회 후 추천 진료과 반환 로직으로 확장
    @Async
    @EventListener
    public void onDepartmentMatched(DepartmentMatchedEvent event) {
        log.info("[진료과 매칭] patientId={}, deptId={}, keyword={}",
                event.patientId(), event.departmentId(), event.keyword());
    }
}
