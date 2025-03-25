package katebu_community.community_be.controller;

import katebu_community.community_be.dto.ApiResponse;
import katebu_community.community_be.exception.DuplicateException;
import katebu_community.community_be.exception.EmailNotFoundException;
import katebu_community.community_be.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원정보 조회
    @GetMapping
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Long userId) {
        // @AuthenticationPrincipal를 통해 JwtAuthenticationFilter에서 설정한 userId를 가져옴

        // 회원정보 조회 로직 호출
        var userDto = userService.getUserById(userId);

        // 실패
        if (userDto == null) {
            // 회원정보가 조회되지 않음
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("회원정보 조회 실패", null));
        }

        // 회원정보 조회 성공
        return ResponseEntity
                .ok(new ApiResponse("회원정보 조회 성공", Map.of("user", userDto)));
    }

    // 회원정보 수정
    @PatchMapping
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "profileImg", required = false) MultipartFile profileImg,
            @RequestParam(value = "nickname", required = false) String nickname) {

        // 필수 입력값 체크
        if (nickname == null || nickname.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("닉네임은 필수 입력값입니다.", null));
        }

        // 회원정보 수정 로직 호출
        try {
            var updatedUser = userService.updateUser(userId, nickname, profileImg);
            // 회원정보 수정 성공
            return ResponseEntity
                    .ok(new ApiResponse("회원정보 수정 성공", Map.of("user", updatedUser)));
        } catch (EmailNotFoundException e) {
            // 회원정보를 찾지 못한 경우
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (DuplicateException e) {
            // 닉네임 중복
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, String> requestBody) {

        // 필수 입력값 체크
        String newPassword = requestBody.get("password");
        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("비밀번호 미입력", null));
        }

        // 비밀번호 변경 로직 호출
        try {
            userService.changePassword(userId, newPassword);
            // 비밀번호 변경 성공
            return ResponseEntity
                    .ok(new ApiResponse("비밀번호 변경 성공", null));
        } catch (EmailNotFoundException e) {
            // 회원정보를 찾지 못한 경우
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal Long userId) {
        // 회원탈퇴 로직 호출
        try {
            userService.deleteUser(userId);
            return ResponseEntity
                    .ok(new ApiResponse("회원탈퇴 성공", null));
        } catch (EmailNotFoundException e) {
            // 회원정보를 찾지 못한 경우
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}