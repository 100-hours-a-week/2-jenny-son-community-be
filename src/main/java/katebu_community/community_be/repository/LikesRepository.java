package katebu_community.community_be.repository;

import katebu_community.community_be.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId); // 특정 게시글+사용자가 좋아요 했는지 확인
    Optional<Likes> findByPostIdAndUserId(Long postId, Long userId); // 특정 게시글+사용자 좋아요 조회
}
