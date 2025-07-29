//package com.busanit501.shoppingweb_project.controller;
//
//import com.busanit501.shoppingweb_project.dto.ReviewRequestDto;
//import com.busanit501.shoppingweb_project.dto.ReviewResponseDto;
//import com.busanit501.shoppingweb_project.service.ReviewService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/products/{productId}/reviews")
//@RequiredArgsConstructor
//public class ReviewController {
//
//    private final ReviewService reviewService;
//
//    @PostMapping
//    public ResponseEntity<ReviewResponseDto> createReview(
//            @PathVariable Long productId,
//            @RequestBody ReviewRequestDto requestDto) {
//        ReviewResponseDto responseDto = reviewService.createReview(productId, requestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsByProductId(@PathVariable Long productId) {
//        List<ReviewResponseDto> reviews = reviewService.getAllReviewsByProductId(productId);
//        return ResponseEntity.ok(reviews);
//    }
//
//    @GetMapping("/{reviewId}")
//    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
//        ReviewResponseDto review = reviewService.getReview(reviewId);
//        return ResponseEntity.ok(review);
//    }
//
//
//    @PutMapping("/{reviewId}")
//    public ResponseEntity<ReviewResponseDto> updateReview(
//            @PathVariable Long reviewId,
//            @RequestBody ReviewRequestDto requestDto) {
//        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, requestDto);
//        return ResponseEntity.ok(updatedReview);
//    }
//
//    @DeleteMapping("/{reviewId}")
//    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
//        reviewService.deleteReview(reviewId);
//        return ResponseEntity.noContent().build();
//    }
//
//}