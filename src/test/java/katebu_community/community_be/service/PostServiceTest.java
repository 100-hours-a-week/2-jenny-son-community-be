package katebu_community.community_be.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import katebu_community.community_be.domain.Post;
import katebu_community.community_be.domain.User;
import katebu_community.community_be.dto.PageableInfoDto;
import katebu_community.community_be.dto.PostListResponseDto;
import katebu_community.community_be.dto.PostSummaryDto;
import katebu_community.community_be.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void logTestStart(TestInfo testInfo) {
        log.info("Starting test: {}", testInfo.getDisplayName());
    }

    @Test
    void 게시글_조회() {
        int page = 0;
        int pageSize = 10;

        // 테스트용 writer 생성
        String email = "test_" + UUID.randomUUID() + "@example.com"; // 테스트마다 고유한 이메일 생성
        User user = User.builder()
                .id(1L)
                .nickname("테스터")
                .email(email)
                .profileUrl("http://example.com/profile.jpg")
                .build();

        // 테스트용 Post 생성
        Post post = Post.builder()
                .id(1L)
                .title("제목")
                .content("내용")
                .createdAt(LocalDateTime.now())
                .likeCnt(5)
                .commentCnt(3)
                .viewCnt(10)
                .user(user)
                .build();

        List<Post> posts = List.of(post);

        // 페이징 설정
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        // postRepository Mock 설정
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

        // 서비스 호출 (PostListResponseDto 반환)
        PostListResponseDto responseDto = postService.getPosts(page);

        // 검증
        // 1) posts 목록 검증
        List<PostSummaryDto> postSummaries = responseDto.getPosts();
        assertThat(postSummaries).hasSize(1);

        PostSummaryDto dto = postSummaries.get(0);
        assertThat(dto.getPostId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("제목");
        assertThat(dto.getWriterId()).isEqualTo(1L);
        assertThat(dto.getWriterName()).isEqualTo("테스터");
        assertThat(dto.getLikeCnt()).isEqualTo(5);
        assertThat(dto.getCommentCnt()).isEqualTo(3);
        assertThat(dto.getViewCnt()).isEqualTo(10);

        // 2) 페이징 정보 검증
        PageableInfoDto pageableInfo = responseDto.getPageable();
        assertThat(pageableInfo.getPageNumber()).isEqualTo(page);
        assertThat(pageableInfo.getPageSize()).isEqualTo(pageSize);

        // 3) 기타 페이징 메타데이터
        assertThat(responseDto.getTotalPages()).isEqualTo(1);
        assertThat(responseDto.getTotalElements()).isEqualTo(1);
        assertThat(responseDto.isLast()).isTrue();
        assertThat(responseDto.isFirst()).isTrue();
        assertThat(responseDto.getSize()).isEqualTo(pageSize);
        assertThat(responseDto.getNumber()).isEqualTo(page);
    }
}
