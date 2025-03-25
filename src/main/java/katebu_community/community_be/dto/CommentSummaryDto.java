package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentSummaryDto {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private Long writerId;
    private String writerName;
    private String writerImg;
    private boolean author;
}