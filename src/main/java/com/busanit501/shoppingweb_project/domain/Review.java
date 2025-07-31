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
}