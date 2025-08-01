package com.busanit501.shoppingweb_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter

@NoArgsConstructor
@Table(name = "reviews")
@Builder
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String reviewContent;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_mid")
    private Member member;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void setProduct(Product product) {
        this.product = product;
        if (!product.getReviews().contains(this)) {
            product.getReviews().add(this);
        }
    }

    // 리뷰 내용, 평점 수정하는 기능.
    public void changeReview(String reviewContent, int rating) {
        this.reviewContent = reviewContent;
        this.rating = rating;
    }
}