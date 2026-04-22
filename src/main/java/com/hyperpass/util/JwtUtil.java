package com.hyperpass.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private static final String DEV_SECRET = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=";

    private final SecretKey secretKey;
    private final long expirationMs;
    private final boolean isDevSecret;

    public JwtUtil(
            @Value("${hyperpass.jwt.secret}") String secret,
            @Value("${hyperpass.jwt.expiration}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
        this.isDevSecret = DEV_SECRET.equals(secret);
    }

    @PostConstruct
    public void warnIfDevSecret() {
        if (isDevSecret) {
            log.warn("===========================================================");
            log.warn("  경고: 개발용 기본 JWT 시크릿을 사용 중입니다.");
            log.warn("  운영 환경에서는 JWT_SECRET 환경변수를 반드시 설정하세요.");
            log.warn("  생성 명령: openssl rand -base64 32");
            log.warn("===========================================================");
        }
    }

    public String generate(Long patientId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(patientId))
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
