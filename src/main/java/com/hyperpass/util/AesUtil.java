package com.hyperpass.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AesUtil {

    private static final String DEV_KEY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec secretKey;
    private final boolean isDevKey;

    public AesUtil(@Value("${hyperpass.security.aes-key}") String keyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES 키는 32바이트(256비트)여야 합니다. 현재: " + keyBytes.length + "바이트");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
        this.isDevKey = DEV_KEY.equals(keyBase64);
    }

    @PostConstruct
    public void warnIfDevKey() {
        if (isDevKey) {
            log.warn("===========================================================");
            log.warn("  경고: 개발용 기본 AES 키를 사용 중입니다.");
            log.warn("  운영 환경에서는 AES_SECRET_KEY 환경변수를 반드시 설정하세요.");
            log.warn("  생성 명령: openssl rand -base64 32");
            log.warn("===========================================================");
        }
    }

    /**
     * 평문을 AES-256 CBC로 암호화하여 "Base64(IV):Base64(CIPHERTEXT)" 형식으로 반환.
     * IV는 호출마다 SecureRandom으로 새로 생성됨.
     */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(iv)
                    + ":" + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    /**
     * "Base64(IV):Base64(CIPHERTEXT)" 형식의 문자열을 복호화하여 평문 반환.
     */
    public String decrypt(String combined) {
        try {
            int sep = combined.indexOf(':');
            if (sep < 0) throw new IllegalArgumentException("잘못된 암호화 포맷 (IV:CIPHERTEXT 형식 아님)");

            byte[] iv = Base64.getDecoder().decode(combined.substring(0, sep));
            byte[] ciphertext = Base64.getDecoder().decode(combined.substring(sep + 1));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}
