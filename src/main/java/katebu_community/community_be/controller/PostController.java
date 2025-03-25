package katebu_community.community_be.controller;

import katebu_community.community_be.dto.ApiResponse;
import katebu_community.community_be.dto.PostDetailDto;
import katebu_community.community_be.dto.PostListResponseDto;
import katebu_community.community_be.exception.AlreadyLikedException;
import katebu_community.community_be.exception.AlreadyUnlikedException;
import katebu_community.community_be.exception.PostNotFoundException;
import katebu_community.community_be.exception.UnauthorizedException;
import katebu_community.community_be.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createPost(
            @AuthenticationPrincipal Long userId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "img", required = false)MultipartFile img) {

        // 필수 입력 값 확인
        if (userId == null ||
                title == null || title.isBlank() ||
                content == null || content.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("요청 형식 오류", null));
        }

        // 게시글 작성 로직 호출
        try {
            Long postId = postService.createPost(userId, title, content, img);
            // 성공
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse("게시글 작성 성공", Map.of("postId", postId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("서버 오류", null));
        }
    }

    // 게시글 수정
    @PatchMapping(value = "/{postid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable("postid") Long postId,
            @AuthenticationPrincipal Long userId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "img", required = false) MultipartFile img) {

        // 필수 입력 값 확인
        if (postId == null || postId <= 0 ||
                title == null || title.isBlank() ||
                content == null || content.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 게시글 수정 로직 호출
        try {
            Long updatedPostId = postService.updatePost(userId, postId, title, content, img);
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("게시글 수정 성공", Map.of("postId", updatedPostId)));
        } catch (PostNotFoundException e) {
            // 게시글 없음
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("게시글 없음", null));
        } catch (UnauthorizedException e) {
            // 권한 없음
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("권한 없음", null));
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{postid}")
    public ResponseEntity<ApiResponse> deletePost(
            @PathVariable("postid") Long postId,
            @AuthenticationPrincipal Long userId) {

        // postId 유효성 검사
        if (postId == null || postId <= 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 게시글 삭제 로직 호출
        try {
            postService.deletePost(userId, postId);
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("게시글 삭제 성공", null));
        } catch (PostNotFoundException e) {
            // 게시글 없음
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("게시글 없음", null));
        } catch (UnauthorizedException e) {
            // 권한 없음
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("권한 없음", null));
        }
    }

    // 게시글 조회
    @GetMapping("/{postid}")
    public ResponseEntity<ApiResponse> getPostDetail(
            @PathVariable("postid") Long postId,
            @AuthenticationPrincipal Long userId) {
        // 필수 입력 값 확인
        if (postId == null || postId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 게시글 조회 로직 호출
        try {
            PostDetailDto postDto = postService.getPostDetail(userId, postId);
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("게시글 조회 성공", postDto));
        } catch (PostNotFoundException e) {
            // 게시글 없음
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("게시글 없음", null));
        }
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam("page") int page) {
        // 게시글 목록 조회 로직 호출
        try {
            PostListResponseDto responseDto = postService.getPosts(page);
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("게시글 목록 조회 성공", responseDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }
    }
}
