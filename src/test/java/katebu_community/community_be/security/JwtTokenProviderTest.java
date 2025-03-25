package katebu_community.community_be.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        // 테스트용 Base64 인코딩된 32바이트(256비트) 비밀키 예제
        // (예: "This is a test secret value!" 를 Base64 인코딩한 문자열)
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "MDEyMzQ1Njc4OUFCQ0RFRjAxMjM0NTY3ODlBQkNERUY=");
        jwtTokenProvider.init(); // @PostConstruct와 동일한 역할 수행
    }

    // 토큰 생성 테스트
    @Test
    public void testCreateToken() {
        Long userId = 123L;
        String email = "test@example.com";

        // 토큰 생성
        String token = jwtTokenProvider.createToken(userId);
        assertNotNull(token, "생성된 토큰은 null이 아니어야 합니다.");

        // 토큰에서 사용자 아이디 추출
        String extractedUserId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals(String.valueOf(userId), extractedUserId, "토큰에 저장된 사용자 아이디가 일치해야 합니다.");

        // 토큰 유효성 검사
        assertTrue(jwtTokenProvider.validateToken(token), "토큰이 유효해야 합니다.");
    }

}