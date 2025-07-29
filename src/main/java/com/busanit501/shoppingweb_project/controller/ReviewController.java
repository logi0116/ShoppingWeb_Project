package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ReviewDTO;
import com.busanit501.shoppingweb_project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 특정 상품에 대한 댓글 목록을 페이징하여 조회하는 API입니다.
     * @param productId 상품 ID
     * @param pageRequestDTO 페이징 요청 정보 (예: /api/reviews/1?page=1&size=5)
     * @return 페이징된 댓글 DTO 목록
     */
    @GetMapping("/{productId}")
    public ResponseEntity<PageResponseDTO<ReviewDTO>> getReviews(
            @PathVariable Long productId,
            PageRequestDTO pageRequestDTO) {

        log.info("Fetching reviews for product: {}, page request: {}", productId, pageRequestDTO);
        PageResponseDTO<ReviewDTO> responseDTO = reviewService.getListOfReviews(productId, pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    // TODO: 댓글 생성(POST), 수정(PUT), 삭제(DELETE) API는 다음 단계에서 구현합니다.

}

/*
========================= 기존 코드 참고 =========================
package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.dto.ReviewRequestDto;
import com.busanit501.shoppingweb_project.dto.ReviewResponseDto;
import com.busanit501.shoppingweb_project.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable Long productId,
            @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto responseDto = reviewService.createReview(productId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsByProductId(@PathVariable Long productId) {
        List<ReviewResponseDto> reviews = reviewService.getAllReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
        ReviewResponseDto review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(review);
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, requestDto);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

}
*/