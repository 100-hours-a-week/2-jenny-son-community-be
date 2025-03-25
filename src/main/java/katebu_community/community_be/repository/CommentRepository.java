package katebu_community.community_be.repository;

import katebu_community.community_be.domain.Comment;
import katebu_community.community_be.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByPostId(Long postId);
    List<Comment> findAllByPostId(Long postId);
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
}
