package katebu_community.community_be.controller;

import katebu_community.community_be.dto.ApiResponse;
import katebu_community.community_be.dto.LoginRequestDto;
import katebu_community.community_be.dto.LoginResponseDto;
import katebu_community.community_be.exception.DuplicateException;
import katebu_community.community_be.exception.EmailNotFoundException;
import katebu_community.community_be.exception.InvalidInputException;
import katebu_community.community_be.exception.InvalidPasswordException;
import katebu_community.community_be.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam(value = "profileImg", required = false) MultipartFile profileImg,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "nickname", required = false) String nickname
    ) {
        // 필수 입력값 체크
        if (profileImg == null || profileImg.isEmpty() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank() ||
                nickname == null || nickname.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "필수 입력값 누락"));
        }

        // 회원가입 로직 호출
        try {
            authService.register(profileImg, email, password, nickname);
            // 회원가입 성공
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "회원가입 성공"));
        } catch (InvalidInputException e) {
            // 닉네임 길이 초과
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (DuplicateException e) {
            // 이메일/닉네임 중복
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
//        catch (IOException e) {
//            // 이미지 업로드 실패
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("message", "회원가입 실패 (이미지 업로드 실패)"));
//        }   // 나머지 예외는 GlobalExceptionHandler 가 처리
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto responseDto = authService.login(loginRequestDto); // 비즈니스 로직
            return ResponseEntity
                    .ok(new ApiResponse("로그인 성공", responseDto));
        } catch (EmailNotFoundException e) {
            // 없는 유저
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (InvalidPasswordException e) {
            // 비밀번호 오류
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
