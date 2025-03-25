package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PostListResponseDto {
    private List<PostSummaryDto> posts;
    private PageableInfoDto pageable; // "pageable": { "pageNumber": 1, "pageSize": 10, "offset": 10 }
    private int totalPages;
    private long totalElements;
    private boolean last;
    private boolean first;
    private int size;
    private int number;
}
