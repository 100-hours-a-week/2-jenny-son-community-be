package katebu_community.community_be.service;

import katebu_community.community_be.domain.Comment;
import katebu_community.community_be.domain.Post;
import katebu_community.community_be.domain.User;
import katebu_community.community_be.repository.CommentRepository;
import katebu_community.community_be.repository.PostRepository;
import katebu_community.community_be.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;

    @Test
    void 회원삭제시_게시글과댓글도_함께삭제() {
        // given - 유저, 게시글, 댓글 저장
        // 테스트용 writer 생성
        String email = "test_" + UUID.randomUUID() + "@example.com"; // 테스트마다 고유한 이메일 생성
        User user = userRepository.save(User.builder()
                .email(email)
                .password("pw")
                .nickname("tester")
                .build());

        Post post = postRepository.save(Post.builder()
                .title("title")
                .content("content")
                .user(user)
                .build());

        commentRepository.save(Comment.builder()
                .content("댓글")
                .post(post)
                .user(user)
                .build());

        // when - 유저 삭제
        userService.deleteUser(user.getId());

        // then - 유저, 게시글, 댓글이 모두 삭제되었는지 확인
        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(postRepository.findById(post.getId())).isEmpty();
        List<Comment> comments = commentRepository.findAllByPostId(post.getId());
        assertThat(comments).isEmpty();
    }
}