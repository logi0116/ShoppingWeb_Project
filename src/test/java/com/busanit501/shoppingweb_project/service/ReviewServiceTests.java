package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.Review;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import com.busanit501.shoppingweb_project.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@Log4j2
public class ReviewServiceTests {

    @Autowired
    private ReviewRepository reviewRepository; // 리뷰 DB 처리를 위한 Repository

    @Autowired
    private ProductRepository productRepository; // 상품 DB 처리를 위한 Repository

    // 리뷰 등록 테스트
    @Test
    public void testInsertReview() {
        Long productId = 5L; // 리뷰를 달 상품의 ID

        Optional<Product> productResult = productRepository.findById(productId);

        if (productResult.isEmpty()) {
            log.info("리뷰를 달 상품이 존재하지 않습니다. ID : " + productId);
            return;
        }

        log.info("상품 조회 성공 - 상품명 : " + productResult.get().getProductName());

        Product product = productResult.get();

        Review review = Review.builder()
                .reviewContent("테스트 리뷰 내용입니다.")
                .rating(5)
                .product(product)
                .build();

        log.info("리뷰 객체 생성 완료 - 내용: , 평점 : " + review.getReviewContent(), review.getRating());

        reviewRepository.save(review);

        log.info("리뷰 등록 완료 - 리뷰 ID : " + review.getReviewId());
    }

    // 리뷰 조회 테스트
    @Test
    @Transactional
    public void testSelectReview() {
        Long reviewId = 2L;
        log.info("리뷰 조회 테스트 시작 - 대상 리뷰 ID : " + reviewId);

        Optional<Review> result = reviewRepository.findById(reviewId);

        if (result.isPresent()) {
            Review review = result.get();
            log.info("리뷰 조회 성공 - 내용 : " + review.getReviewContent());
            log.info("평점 : " + review.getRating());
            log.info("연결된 상품명 : " + review.getProduct().getProductName());
        } else {
            log.info("해당 ID의 리뷰가 존재하지 않습니다. ID : " + reviewId);
        }
    }

    // 리뷰 수정 테스트
    @Test
    public void testUpdateReview() {
        Long reviewId = 2L;
        log.info("리뷰 수정 테스트 시작 - 대상 리뷰 ID : " + reviewId);

        Optional<Review> result = reviewRepository.findById(reviewId);

        if (result.isPresent()) {
            Review review = result.get();
            log.info("수정 전 리뷰 내용 : , 평점 : " + review.getReviewContent(), review.getRating());

            review.changeReview("수정된 리뷰 내용입니다.", 4); // 새로 만든 메서드로 내용과 평점을 한번에 수정

            reviewRepository.save(review);
            log.info("리뷰 수정 완료 - 내용 : , 평점 : " + review.getReviewContent(), review.getRating());
        } else {
            log.info("수정할 리뷰가 존재하지 않습니다. ID : " + reviewId);
        }
    }

    // 리뷰 삭제 테스트
    @Test
    public void testDeleteReview() {
        Long reviewId = 2L;
        log.info("리뷰 삭제 테스트 시작 - 대상 리뷰 ID: " + reviewId);

        Optional<Review> result = reviewRepository.findById(reviewId);

        if (result.isPresent()) {
            reviewRepository.deleteById(reviewId);
            log.info("리뷰가 성공적으로 삭제되었습니다. ID : " + reviewId);
        } else {
            log.info("삭제할 리뷰가 존재하지 않습니다. ID : " + reviewId);
        }
    }
}
