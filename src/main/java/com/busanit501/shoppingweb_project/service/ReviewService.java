//package com.busanit501.shoppingweb_project.service;
//
//import com.busanit501.shoppingweb_project.domain.Product;
//import com.busanit501.shoppingweb_project.domain.Review;
//import com.busanit501.shoppingweb_project.repository.ProductRepository;
//import com.busanit501.shoppingweb_project.repository.ReviewRepository;
//import com.busanit501.shoppingweb_project.dto.ReviewproductDTO;
//import com.busanit501.shoppingweb_project.dto.ProductDTO;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ReviewService {
//
//    private final ReviewRepository reviewRepository;
//    private final ProductRepository productRepository;
//
//    public ProductDTO createReview(Long productId, ProductDTO productDTO) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. 상품 ID: " + productId));
//
//        Review review = new Review();
//        review.setReviewContent(productDTO.getReviewContent());
//        review.setRating(productDTO.getRating());
//        review.setProduct(product);
//
//        product.addReview(review);
//
//        Review savedReview = reviewRepository.save(review);
//
//        return new ProductDTO(savedReview);
//    }
//
//    public List<ProductDTO> getAllReviewsByProductId(Long productId) {
//        List<Review> reviews = reviewRepository.findByProduct_ProductId(productId);
//
//        return reviews.stream()
//                .map(ProductDTO::new)
//                .collect(Collectors.toList());
//    }
//
//    public ProductDTO getReview(Long reviewId) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. 리뷰 ID: " + reviewId));
//        return new ProductDTO(review);
//    }
//
//    // updateReview, deleteReview 메서드 추가 시작
//    public ProductDTO updateReview(Long reviewId, ReviewproductDTO productDTO) {
//        Review review = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. 리뷰 ID: " + reviewId));
//
//        review.setReviewContent(productDTO.getReviewContent());
//        review.setRating(productDTO.getRating());
//
//        return new ProductDTO(review);
//    }
//
//    public void deleteReview(Long reviewId) {
//        if (!reviewRepository.existsById(reviewId)) {
//            throw new IllegalArgumentException("삭제할 리뷰를 찾을 수 없습니다. 리뷰 ID: " + reviewId);
//        }
//        reviewRepository.deleteById(reviewId);
//    }
//
//}