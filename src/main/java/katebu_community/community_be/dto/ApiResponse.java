package katebu_community.community_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

// 표준 응답 DTO
@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private Object data;
}