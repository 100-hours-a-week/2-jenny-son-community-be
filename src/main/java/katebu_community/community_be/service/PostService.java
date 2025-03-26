package katebu_community.community_be.service;

import katebu_community.community_be.domain.Likes;
import katebu_community.community_be.domain.Post;
import katebu_community.community_be.domain.User;
import katebu_community.community_be.dto.*;
import katebu_community.community_be.exception.AlreadyLikedException;
import katebu_community.community_be.exception.AlreadyUnlikedException;
import katebu_community.community_be.exception.PostNotFoundException;
import katebu_community.community_be.exception.UnauthorizedException;
import katebu_community.community_be.repository.CommentRepository;
import katebu_community.community_be.repository.LikesRepository;
import katebu_community.community_be.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final FileUploadService fileUploadService;
    private final UserCommonService userCommonService;

    // 게시글 작성
    @Transactional
    public Long createPost(Long userId, String title, String content, MultipartFile img) {
        // 회원 조회
        User writer = userCommonService.getUserOrThrow(userId);

        // 이미지가 제공되면 업로드 후 URL 획득
        String imageUrl = null;
        if (img != null && !img.isEmpty()) {
            imageUrl = fileUploadService.uploadImage(img);
        }

        // 게시글 엔티티 생성 및 저장
        Post post = Post.builder()
                .user(writer)
                .title(title)
                .content(content)
                .imgUrl(imageUrl)
                .build();
        Post saved = postRepository.save(post);
        return saved.getId();
    }

    // 게시글 수정
    @Transactional
    public Long updatePost(Long userId, Long postId, String title, String content, MultipartFile img) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 수정 권한 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("권한 없음");
        }

        // 게시글 수정
        post.setTitle(title);
        post.setContent(content);

        if (img != null && !img.isEmpty()) {
            fileUploadService.deleteImage(post.getImgUrl()); // 기존 이미지 삭제
            // 새 이미지 업로드
            String imageUrl = fileUploadService.uploadImage(img);
            post.setImgUrl(imageUrl);
        }

        Post saved = postRepository.save(post);
        return saved.getId();
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long userId, Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 삭제 권한 확인
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("권한 없음");
        }

        // 게시글 삭제 전 댓글 삭제
        commentRepository.deleteByPostId(postId);

        // 본문 이미지 삭제
        fileUploadService.deleteImage(post.getImgUrl());

        // 게시글 삭제
        postRepository.delete(post);
    }

    // 게시글 조회
    @Transactional
    public PostDetailDto getPostDetail(
            Long userId,
            Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 조회수 증가
        post.setViewCnt(post.getViewCnt() + 1);

        // 엔티티를 PostDetailDto로 변환해서 반환
        return PostDetailDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .img(post.getImgUrl())
                .createdAt(post.getCreatedAt())
                .likeCnt(post.getLikeCnt())
                .commentCnt(post.getCommentCnt())
                .viewCnt(post.getViewCnt())
                .writer(WriterDto.builder()
                        .writerId(post.getUser().getId())
                        .writerName(post.getUser().getNickname())
                        .writerImg(post.getUser().getProfileUrl())
                        .build())
                .liked(userId != null && likesRepository.existsByPostIdAndUserId(postId, userId))
                .author(userId != null && userId.equals(post.getUser().getId()))
                .build();
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public PostListResponseDto getPosts(int page) {

        // 페이지 값 유효성 검사
        int pageSize = 10;
        if (page < 0) {
            throw new IllegalArgumentException("유효하지 않은 페이지 값");
        }

        // 게시글 목록 조회
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);

        // 게시글 목록을 PostSummaryDto 리스트로 변환
        List<PostSummaryDto> posts = postPage.getContent().stream().map(post ->
                PostSummaryDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .createdAt(post.getCreatedAt())
                        .likeCnt(post.getLikeCnt())
                        .commentCnt(post.getCommentCnt())
                        .viewCnt(post.getViewCnt())
                        .writerId(post.getUser().getId())
                        .writerName(post.getUser().getNickname())
                        .writerImg(post.getUser().getProfileUrl())
                        .build()
        ).collect(Collectors.toList());

        // 페이징 정보
        PageableInfoDto pageableInfo = PageableInfoDto.builder()
                .pageNumber(postPage.getNumber())
                .pageSize(postPage.getSize())
                .offset(postPage.getPageable().getOffset())
                .build();

        // PostListResponseDto로 data 객체를 반환
        return PostListResponseDto.builder()
                .posts(posts)
                .pageable(pageableInfo)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .last(postPage.isLast())
                .first(postPage.isFirst())
                .size(postPage.getSize())
                .number(postPage.getNumber())
                .build();
    }

    // 좋아요 추가
    @Transactional
    public void addLike(Long userId, Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 중복 좋아요 여부 확인
        if (likesRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new AlreadyLikedException("이미 좋아요를 눌렀습니다.");
        }

        // 좋아요 추가
        Likes like = Likes.builder()
                .postId(postId)
                .userId(userId)
                .build();
        likesRepository.save(like);

        // 게시글의 좋아요 수 증가
        post.setLikeCnt(post.getLikeCnt() + 1);
        postRepository.save(post);
    }

    // 좋아요 삭제
    @Transactional
    public void deleteLike(Long userId, Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글 없음"));

        // 좋아요 조회
        Likes like = likesRepository.findByPostIdAndUserId(postId, userId)
                .orElse(null);

        if (like != null) {
            // 좋아요 삭제
            likesRepository.delete(like);
            // 게시글의 좋아요 수 감소
            if (post.getLikeCnt() > 0) {
                post.setLikeCnt(post.getLikeCnt() - 1);
            } else {
                post.setLikeCnt(0);
            }
            postRepository.save(post);
        } else {
            throw new AlreadyUnlikedException("이미 좋아요를 취소했습니다.");
        }
    }
}
