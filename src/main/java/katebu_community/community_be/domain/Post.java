package katebu_community.community_be.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private Long id;

    // 작성자 (User와 연관관계 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 26)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name="img_url", length = 500)
    private String imgUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "view_cnt", nullable = false)
    private int viewCnt;

    @Column(name = "like_cnt", nullable = false)
    private int likeCnt;

    @Column(name = "comment_cnt", nullable = false)
    private int commentCnt;

    @PrePersist
    protected void onCreate() {
        this.viewCnt = 0;
        this.likeCnt = 0;
        this.commentCnt = 0;
    }
}
