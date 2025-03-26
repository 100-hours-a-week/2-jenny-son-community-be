package katebu_community.community_be.service;

import katebu_community.community_be.domain.Comment;
import katebu_community.community_be.domain.Post;
import katebu_community.community_be.domain.User;
import katebu_community.community_be.dto.CommentListResponseDto;
import katebu_community.community_be.dto.CommentSummaryDto;
import katebu_community.community_be.exception.CommentNotFoundException;
import katebu_community.community_be.exception.PostNotFoundException;
import katebu_community.community_be.exception.UnauthorizedException;
import katebu_community.community_be.repository.CommentRepository;
import katebu_community.community_be.repository.PostRepository;
import katebu_community.community_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserCommonService userCommonService;

    // 댓글 작성
    @Transactional
    public Long createComment(Long userId, Long postId, String content) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 회원 조회
        User writer = userCommonService.getUserOrThrow(userId);

        // 댓글 엔티티 생성 및 저장
        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .user(writer)
                .build();
        Comment savedComment = commentRepository.save(comment);

        // 게시글 댓글 수 증가
        post.setCommentCnt(post.getCommentCnt() + 1);
        postRepository.save(post);

        return savedComment.getId();
    }

    // 댓글 수정
    @Transactional
    public Long updateComment(Long userId, Long postId, Long commentId, String content) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글 없음"));

        // 게시글 ID 일치 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("잘못된 요청: 게시글 ID 불일치");
        }

        // 권한 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("권한 없음");
        }

        // 댓글 내용 업데이트 후 저장
        comment.setContent(content);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long userId, Long postId, Long commentId) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글 없음"));

        // 게시글 ID 일치 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("잘못된 요청: 게시글 ID 불일치");
        }

        // 권한 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("권한 없음");
        }

        // 댓글 삭제
        commentRepository.delete(comment);

        // 게시글의 댓글 수 감소
        Post post = comment.getPost();
        post.setCommentCnt(post.getCommentCnt() - 1);
        postRepository.save(post);
    }

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public CommentListResponseDto getComments(Long userId, Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 댓글 목록 조회
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);

        // 댓글 목록을 CommentSummaryDto 리스트로 생성
        List<CommentSummaryDto> commentList = comments.stream().map(comment ->
                CommentSummaryDto.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .writerId(comment.getUser().getId())
                        .writerName(comment.getUser().getNickname())
                        .writerImg(comment.getUser().getProfileUrl())
                        .author(userId != null && comment.getUser().getId().equals(userId))
                        .build()
        ).collect(Collectors.toList());

        return CommentListResponseDto.builder()
                .postId(post.getId())
                .commentCnt(commentList.size())
                .commentList(commentList)
                .build();
    }
}
