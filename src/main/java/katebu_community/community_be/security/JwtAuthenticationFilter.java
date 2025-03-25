package katebu_community.community_be.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // 토큰 검증 필터를 건너뛰도록 처리
        // 로그인, 회원가입
        if (path.startsWith("/auth")) {
            return true;
        }
        // 게시글 목록 조회
        if ("GET".equalsIgnoreCase(request.getMethod()) && path.matches("/posts")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 이미지 요청
        String uri = request.getRequestURI();
        if (uri.startsWith("/uploads/")) {
            chain.doFilter(request, response);
            return;
        }

        // Authroization 헤더에서 토큰 추출 ("Bearer " 이후 값)
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 토큰 값
            String token = authorizationHeader.substring(7);

            // 토큰에서 사용자 ID 추출
            if (jwtTokenProvider.validateToken(token)) {
                String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
                try {
                    Long userId = Long.parseLong(userIdStr);
                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                    // SecurityContext에 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                    return;
                }
            } else {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }
        } else {
            // 게시글 상세 조회 요청 & 댓글 목록 조회 요청은 토큰이 없어도 접근 가능하도록 통과 (토큰이 있다면 if문 안에서 ID 추출)
            String path = request.getServletPath();
            if ("GET".equalsIgnoreCase(request.getMethod()) &&
                    (path.matches("^/posts/\\d+$") || path.matches("^/posts/\\d+/comments$"))) {
                chain.doFilter(request, response);
                return;
            }

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 정보가 누락되었습니다.");
            return;
        }

        // 다음 필터로 이동
        chain.doFilter(request, response);
    }
}
