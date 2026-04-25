package com.hyperpass.controller;

import com.hyperpass.domain.HpPatient;
import com.hyperpass.security.JwtAuthenticationFilter;
import com.hyperpass.service.HpPatientService;
import com.hyperpass.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HpPatientService hpPatientService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("환자 인증 및 토큰 발급 테스트")
    void verify_Success() throws Exception {
        // given
        HpPatient patient = new HpPatient();
        patient.setId(1L);
        given(hpPatientService.findOrCreate(any())).willReturn(patient);
        given(jwtUtil.generate(anyLong(), anyString())).willReturn("mock-token");

        // when & then
        mockMvc.perform(post("/api/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"홍길동\", \"rrn\":\"900101-1234567\", \"address\":\"서울시 강남구\", \"phone\":\"010-1234-5678\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-token"));
    }

    @Test
    @DisplayName("주민등록번호 형식 오류 시 400 반환 테스트")
    void verify_Fail_InvalidRrn() throws Exception {
        mockMvc.perform(post("/api/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"홍길동\", \"rrn\":\"9001011234567\", \"address\":\"서울시 강남구\", \"phone\":\"010-1234-5678\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(hpPatientService, jwtUtil);
    }
}
