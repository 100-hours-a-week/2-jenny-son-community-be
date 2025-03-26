package katebu_community.community_be.controller;

import katebu_community.community_be.dto.ApiResponse;
import katebu_community.community_be.dto.CommentListResponseDto;
import katebu_community.community_be.dto.CommentRequestDto;
import katebu_community.community_be.exception.CommentNotFoundException;
import katebu_community.community_be.exception.PostNotFoundException;
import katebu_community.community_be.exception.UnauthorizedException;
import katebu_community.community_be.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts/{postid}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable("postid") Long postId,
            @AuthenticationPrincipal Long userId,
            @RequestBody CommentRequestDto requestDto) {

        // 필수 입력값 검증
        if (requestDto.getContent() == null || requestDto.getContent().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 댓글 작성 로직 호출
        try {
            Long commentId = commentService.createComment(userId, postId, requestDto.getContent());
            // 성공
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse("댓글 작성 성공", Map.of("commentId", commentId)));
        } catch (PostNotFoundException e) {
            // 게시글 조회 실패
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("게시글 없음", null));
        }
    }

    // 댓글 수정
    @PatchMapping(value = "/{commentid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateComment(
            @PathVariable("postid") Long postId,
            @PathVariable("commentid") Long commentId,
            @AuthenticationPrincipal Long userId,
            @RequestBody CommentRequestDto requestDto) {

        // 필수 입력값 검증
        if (requestDto.getContent() == null || requestDto.getContent().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 댓글 수정 로직 호출
        try {
            Long updatedCommentId = commentService.updateComment(userId, postId, commentId, requestDto.getContent());
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("댓글 수정 성공", Map.of("commentId", updatedCommentId)));
        } catch (CommentNotFoundException e) {
            // 댓글 조회 실패
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("댓글 없음", null));
        } catch (UnauthorizedException e) {
            // 권한 없음
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("권한 없음", null));
        } catch (IllegalArgumentException e) {
            // 게시글 ID 불일치
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("게시글 ID 불일치", null));
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentid}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable("postid") Long postId,
            @PathVariable("commentid") Long commentId,
            @AuthenticationPrincipal Long userId) {

        // 필수 입력 값 검증
        if (postId == null || postId <= 0 || commentId == null || commentId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 댓글 삭제 로직 호출
        try {
            commentService.deleteComment(userId, postId, commentId);
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("댓글 삭제 성공", null));
        } catch (CommentNotFoundException e) {
            // 댓글 없음
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("댓글 없음", null));
        } catch (UnauthorizedException e) {
            // 권한 없음
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("권한 없음", null));
        } catch (IllegalArgumentException e) {
            // 게시글 ID 불일치
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("게시글 ID 불일치", null));
        }
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getComments(
            @PathVariable("postid") Long postId,
            @AuthenticationPrincipal Long userId) {
        // postId 유효성 검사
        if (postId == null || postId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("잘못된 요청", null));
        }

        // 댓글 목록 조회 로직 호출
        try {
            CommentListResponseDto responseDto = commentService.getComments(userId, postId);
            // 성공
            return ResponseEntity
                    .ok(new ApiResponse("댓글 목록 조회 성공", responseDto));
        } catch (PostNotFoundException e) {
            // 게시글 없음
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("게시글 없음", null));
        }
    }
}
