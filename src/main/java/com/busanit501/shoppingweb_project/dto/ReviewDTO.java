package com.busanit501.shoppingweb_project.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    @NotEmpty
    private String reviewContent;
    @NotEmpty
    private int rating;

    @CreatedDate
    private LocalDateTime createdAt;
    private Long productId; // productId 필드 추가
}
