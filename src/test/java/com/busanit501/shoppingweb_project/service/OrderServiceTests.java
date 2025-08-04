package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Address;
import com.busanit501.shoppingweb_project.domain.CartItem;
import com.busanit501.shoppingweb_project.domain.Member;
import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.repository.AddressRepository;
import com.busanit501.shoppingweb_project.repository.CartItemRepository;
import com.busanit501.shoppingweb_project.repository.MemberRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
 
 @SpringBootTest
 @Log4j2
public class OrderServiceTests {

        @Autowired
        private OrderService orderService;

        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private CartItemRepository cartItemRepository;
        @Autowired
        private MemberRepository memberRepository;
        @Autowired
        private AddressRepository addressRepository;
 
         @Test
         public void testCreateOrder() {
            // Given: 테스트를 위한 사전 데이터 설정
            // 1. 테스트용 회원 생성 및 저장
            Member member = Member.builder()
                    .memberId("testuser")
                    .password("1234")
                    .userName("테스트유저")
                    .email("test@example.com")
                    .role("USER")
                    .birthDate(LocalDate.now())
                    .build();
            memberRepository.save(member);

            // 2. 테스트용 기본 배송지 생성 및 저장
            Address address = Address.builder()
                    .member(member)
                    .addressId("12345")
                    .addressLine("부산시 테스트구 테스트동")
                    .isDefault(true) // 기본 배송지로 설정
                    .build();
            addressRepository.save(address);
                // 더미 데이터
                Product product = Product.builder()
                                .productName("바지")
                                .price(BigDecimal.valueOf(40000))
                                .stock(10)
                                .build();
                productRepository.save(product);
                Product product2 = Product.builder()
                                .productName("겉옷")
                                .price(BigDecimal.valueOf(60000))
                                .stock(10)
                                .build();
                productRepository.save(product2);
                log.info("testCreateOrder에서 서비스 테스트중.." + product + product2);
                CartItem cartItem = CartItem.builder()
                                .memberId(member.getId())
                                .product(product)
                                .quantity(2)
                                .build();
                cartItemRepository.save(cartItem);
                CartItem cartItem2 = CartItem.builder()
                                .memberId(member.getId())
                                .product(product2)
                                .quantity(2)
                                .build();
                cartItemRepository.save(cartItem2);
                log.info("testCreateOrder에서 서비스 테스트중.. cartItem : " + cartItem + cartItem2);
                orderService.PurchaseFromCart(member.getId());
         }
}
