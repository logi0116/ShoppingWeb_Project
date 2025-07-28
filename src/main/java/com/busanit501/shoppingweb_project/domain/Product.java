package com.busanit501.shoppingweb_project.domain;

import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private BigDecimal price;
    private int stock;

    @Enumerated(EnumType.STRING)
    private ProductCategory productTag;

    public void removeStock(int quantity) {
        int restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.stock);
        }
        this.stock = restStock;
    }

    public void addStock(int quantity) {
        this.stock += quantity;
    }
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        cartItem.setCartItems(this); // FK 설정
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();


    public void addReview(Review review) {
        this.reviews.add(review);
        review.setProduct(this);
    }


    public void removeReview(Review review) {
        this.reviews.remove(review);
        review.setProduct(null);
    }
}


