package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostSummaryDto {
    private Long postId;
    private String title;
    private LocalDateTime createdAt;
    private int likeCnt;
    private int commentCnt;
    private int viewCnt;
    private Long writerId;
    private String writerName;
    private String writerImg;
}
