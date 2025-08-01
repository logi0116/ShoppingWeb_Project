package com.busanit501.shoppingweb_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블 이름 명시
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "orderItems", "member" }) // 순환 참조 방지

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId") // PK
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "orderdate")
    private LocalDateTime orderDate;

    @Column(name = "status")
    @Builder.Default
    private Boolean status = false;

    private String address;
    private String addressDetail;
    @Builder.Default
    private int totalPrice = 0;
    private String receiverName;
    private String receiverPhone;
    // 양방향 연관관계 설정 - 비주인
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this); // FK 설정
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
