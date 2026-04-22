package com.hyperpass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisitRequest {

    private Long kioskId;
    private Long departmentId;
    private String symptomKeyword;  // 선택: 증상 키워드 → 진료과 자동 매칭 이벤트 발행
    private String hisPatientNo;    // 선택: HIS 환자번호 (HIS 연동 시 사용)
    private String hospitalCode;    // 선택: 병원 코드 (다기관 확장 시 사용)
}
