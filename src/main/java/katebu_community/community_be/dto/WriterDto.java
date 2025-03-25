package katebu_community.community_be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WriterDto {
    private Long writerId;
    private String writerName;
    private String writerImg;
}