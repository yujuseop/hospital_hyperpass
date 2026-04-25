package com.hyperpass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주민등록번호는 필수입니다.")
    @Pattern(regexp = "^\\d{6}-\\d{7}$", message = "주민등록번호 형식은 000000-0000000 이어야 합니다.")
    private String rrn;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "휴대폰번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "휴대폰번호 형식은 010-1234-5678 이어야 합니다.")
    private String phone;
}
