package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ReviewDTO;
import com.busanit501.shoppingweb_project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public ResponseEntity<PageResponseDTO<ReviewDTO>> getReviewList(
            @PathVariable Long productId,
            PageRequestDTO pageRequestDTO) {
        log.info("Fetching reviews for productId: {}, pageRequest: {}", productId, pageRequestDTO);
        PageResponseDTO<ReviewDTO> responseDTO = reviewService.getReviewList(productId, pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<?> registerReview(@RequestBody ReviewDTO reviewDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            String memberId = userDetails.getUsername();
            Long reviewId = reviewService.registerReview(reviewDTO, memberId);
            return ResponseEntity.status(HttpStatus.CREATED).body(reviewId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("리뷰 등록 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 등록 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/check-purchase")
    public ResponseEntity<Boolean> checkPurchaseStatus(@RequestParam Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(false); // 비로그인 사용자는 구매 이력 없음
        }
        String memberId = userDetails.getUsername();
        boolean hasPurchased = reviewService.checkIfUserPurchasedProduct(memberId, productId);
        return ResponseEntity.ok(hasPurchased);
    }
}