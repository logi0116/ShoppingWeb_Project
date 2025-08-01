package com.busanit501.shoppingweb_project.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    // 회원 ID (연관관계 매핑도 가능하지만, 단순 Long으로 두는 경우 많음)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 상품 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 수량
    @Column(nullable = false)
    @Builder.Default
    private int quantity = 1;

    public void setCartItems(Product product) {
        this.product = product;

        // 양방향이니까 Order에도 나(this)를 추가해줘야 함
        if (!product.getCartItems().contains(this)) {
            product.getCartItems().add(this);
        }
    }

    public void changeQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}
