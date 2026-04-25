package com.hyperpass.controller;

import com.hyperpass.exception.ReceptionApprovalException;
import com.hyperpass.security.JwtAuthenticationFilter;
import com.hyperpass.dto.PendingReceptionResponse;
import com.hyperpass.service.HpReceptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false) // 인증 필터 임시 비활성화
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HpReceptionService hpReceptionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("초진 환자 신분증 미확인 시 승인 불가 테스트")
    void approve_Fail_IfNotIdVerified() throws Exception {
        // given: 서비스에서 예외를 던지도록 설정
        Long receptionId = 1L;
        Long staffId = 99L;
        Long deptId = 1L;

        given(hpReceptionService.approve(eq(receptionId), eq(staffId), eq(deptId)))
                .willThrow(new ReceptionApprovalException("초진 환자는 신분증 확인 완료 후 승인할 수 있습니다."));

        // when & then
        mockMvc.perform(patch("/api/admin/receptions/{id}/approve", receptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"departmentId\": 1}")
                        .principal(new UsernamePasswordAuthenticationToken("99", null)))
                .andExpect(status().isBadRequest()) // 예상되는 에러 상태
                .andExpect(jsonPath("$.message").value("초진 환자는 신분증 확인 완료 후 승인할 수 있습니다."));
    }

    @Test
    @DisplayName("대기 목록 조회 테스트")
    void getPending_Success() throws Exception {
        // given
        List<PendingReceptionResponse> list = List.of(new PendingReceptionResponse());
        given(hpReceptionService.getPending()).willReturn(list);

        // when & then
        mockMvc.perform(get("/api/admin/receptions/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("신분증 확인 완료 처리 테스트")
    void verifyId_Success() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/admin/receptions/1/verify-id")
                        .principal(new UsernamePasswordAuthenticationToken("99", null)))
                .andExpect(status().isNoContent());

        verify(hpReceptionService, times(1)).verifyId(eq(1L), eq(99L));
    }
}
