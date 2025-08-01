package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.CartItem;
import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.repository.CartItemRepository;
import com.busanit501.shoppingweb_project.repository.OrderRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
@Log4j2
public class OrderServiceTests {

        @Autowired
        private OrderService orderService;

        @Autowired
        private ProductRepository productRepository;
        @Autowired
        private CartItemRepository cartItemRepository;

        @Test
        public void testCreateOrder() {
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
                                .memberId(1L)
                                .product(product)
                                .quantity(2)
                                .build();
                cartItemRepository.save(cartItem);
                CartItem cartItem2 = CartItem.builder()
                                .memberId(1L)
                                .product(product2)
                                .quantity(2)
                                .build();
                cartItemRepository.save(cartItem2);
                log.info("testCreateOrder에서 서비스 테스트중.. cartItem : " + cartItem + cartItem2);
                orderService.PurchaseFromCart(1L);
        }
}
