package com.hyperpass;

import com.hyperpass.domain.HpStaff;
import com.hyperpass.mapper.HpStaffMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final HpStaffMapper hpStaffMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (hpStaffMapper.findByUsername("admin") == null) {
            HpStaff admin = new HpStaff();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setName("관리자");
            admin.setRole("ADMIN");
            hpStaffMapper.insert(admin);
            log.info("[DataInitializer] 기본 관리자 계정 생성 완료 (admin / admin1234)");
        }
    }
}
