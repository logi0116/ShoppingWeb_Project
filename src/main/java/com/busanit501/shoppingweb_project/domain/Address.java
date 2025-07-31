package com.busanit501.shoppingweb_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address")
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String zipcode;
    private String addressId;
    private String addressLine;
    private LocalDateTime createdAt;

    private boolean isDefault; // 기본 배송지 여부

    // N(배송지) : 1(회원)
    // 하나의 회원에 여러개의 배송지를 입력할 수 있도록 만들겠습니다.
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
