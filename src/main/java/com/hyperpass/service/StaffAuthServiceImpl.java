package com.hyperpass.service;

import com.hyperpass.domain.HpStaff;
import com.hyperpass.dto.StaffLoginRequest;
import com.hyperpass.dto.StaffLoginResponse;
import com.hyperpass.mapper.HpStaffMapper;
import com.hyperpass.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffAuthServiceImpl implements StaffAuthService {

    private final HpStaffMapper hpStaffMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public StaffLoginResponse login(StaffLoginRequest request) {
        HpStaff staff = hpStaffMapper.findByUsername(request.getUsername());
        if (staff == null || !passwordEncoder.matches(request.getPassword(), staff.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.generate(staff.getId(), "ADMIN");

        return StaffLoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600)
                .name(staff.getName())
                .build();
    }
}
