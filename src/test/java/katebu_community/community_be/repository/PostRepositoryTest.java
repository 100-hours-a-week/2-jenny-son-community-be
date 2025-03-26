package katebu_community.community_be.repository;

import katebu_community.community_be.domain.Post;
import katebu_community.community_be.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@DataJpaTest // JPA 관련 빈만 로드
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void logTestStart(TestInfo testInfo) {
        log.info("Starting test: {}", testInfo.getDisplayName());
    }

    @Test
    void 게시글_생성_조회() {
        String email = "test_" + UUID.randomUUID() + "@example.com"; // 테스트마다 고유한 이메일 생성

        // 1) 사용자 생성
        User user = User.builder()
                .email(email)
                .password("1234")
                .nickname("테스터")
                .build();
        User savedUser = userRepository.save(user); // 영속화된 User 객체

        // 2) 게시글 생성
        Post post = Post.builder()
                .user(savedUser)      // 연관관계 설정
                .title("제목")
                .content("내용")
                .build();
        Post savedPost = postRepository.save(post);

        // 3) 게시글 조회
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getTitle()).isEqualTo("제목");
        assertThat(foundPost.get().getUser().getNickname()).isEqualTo("테스터");
    }

    @Test
    void 모든게시글_조회() {
        // 고유한 이메일 생성 후 User 생성 및 영속화
        String email = "test_" + UUID.randomUUID() + "@example.com";
        User user = User.builder()
                .email(email)
                .password("1234")
                .nickname("테스터")
                .build();
        User savedUser = userRepository.save(user);

        // 1) 게시글 여러 개 저장
        List<Post> posts = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> Post.builder()
                        .user(savedUser)
                        .title("제목" + i)
                        .content("내용" + i)
                        // writer, createdAt 등 필요한 필드 세팅
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(posts);

        // 2) findAll로 조회
        List<Post> allPosts = postRepository.findAll();
        assertThat(allPosts.size()).isGreaterThanOrEqualTo(5);
    }


}