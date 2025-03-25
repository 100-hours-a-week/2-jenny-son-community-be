package katebu_community.community_be.security;

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
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    private final long tokenValidTime = 1000L * 60 * 60 * 2; // 2시간 유효

    @PostConstruct // 의존성 주입이 이루어진 후 초기화를 수행하는 메서드에 사용
    protected void init() {
        // 주입받은 secret을 Base64로 디코딩하여 SecretKey 생성
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String createToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidTime);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // 사용자 정보 저장
                .issuedAt(now)                  // 발급 시간
                .expiration(expiryDate)         // 만료 시간
                .signWith(key)                  // 서명
                .compact();                     // JWS(최종 서명된 JWT) 문자열 생성
    }

    // 토큰에서 사용자 정보(id) 추출
    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            log.error("JWT Token 사용자 정보 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT Token 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}
