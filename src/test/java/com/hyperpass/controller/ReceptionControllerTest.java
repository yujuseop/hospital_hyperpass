package com.hyperpass.controller;

import com.hyperpass.dto.PreCheckInResponse;
import com.hyperpass.security.JwtAuthenticationFilter;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReceptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HpReceptionService hpReceptionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("사전 문진 제출 성공 테스트")
    void preCheckIn_Success() throws Exception {
        // given
        PreCheckInResponse response = PreCheckInResponse.builder()
                .receptionId(1L)
                .build();
        given(hpReceptionService.preCheckIn(anyLong(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/receptions/precheckin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mainSymptom\":\"감기\", \"painLevel\":3}")
                        .principal(new UsernamePasswordAuthenticationToken("1", null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.receptionId").value(1L));
    }
}
