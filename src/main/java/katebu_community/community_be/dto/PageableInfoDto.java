package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageableInfoDto {
    private int pageNumber;
    private int pageSize;
    private long offset;
}
