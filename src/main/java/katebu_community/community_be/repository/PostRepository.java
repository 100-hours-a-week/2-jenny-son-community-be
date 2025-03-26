package katebu_community.community_be.repository;

import katebu_community.community_be.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    List<Post> findAllByOrderByCreatedAtDesc(); // 최신순 정렬
}
