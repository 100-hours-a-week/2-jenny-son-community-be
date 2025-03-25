package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostDetailDto {
    private Long postId;
    private String title;
    private String content;
    private String img;              // 이미지 URL (없으면 null)
    private LocalDateTime createdAt;
    private int likeCnt;
    private int commentCnt;
    private int viewCnt;
    private WriterDto writer;        // 작성자 정보 객체
    private boolean liked;
    private boolean author;
}