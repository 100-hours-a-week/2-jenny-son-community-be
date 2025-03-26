package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CommentListResponseDto {
    private Long postId;
    private int commentCnt;
    private List<CommentSummaryDto> commentList;
}
