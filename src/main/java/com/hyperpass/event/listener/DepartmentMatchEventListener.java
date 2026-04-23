package com.hyperpass.event.listener;

import com.hyperpass.dto.DepartmentRecommendResponse;
import com.hyperpass.event.DepartmentMatchedEvent;
import com.hyperpass.service.DepartmentMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentMatchEventListener {

    private final DepartmentMatchService departmentMatchService;

    @Async
    @EventListener
    public void onDepartmentMatched(DepartmentMatchedEvent event) {
        if (event.keyword() == null) return;

        List<DepartmentRecommendResponse> recommendations =
                departmentMatchService.recommend(event.keyword());

        if (!recommendations.isEmpty()) {
            log.info("[진료과 매칭] patientId={}, keyword={} → 추천 1순위: {}",
                    event.patientId(), event.keyword(),
                    recommendations.get(0).getDepartmentName());
        } else {
            log.info("[진료과 매칭] patientId={}, keyword={} → 매핑 없음",
                    event.patientId(), event.keyword());
        }
    }
}
