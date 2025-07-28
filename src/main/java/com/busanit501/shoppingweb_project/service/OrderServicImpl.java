package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.CartItem;
import com.busanit501.shoppingweb_project.domain.Order;
import com.busanit501.shoppingweb_project.domain.OrderItem;
import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.repository.CartItemRepository;
import com.busanit501.shoppingweb_project.repository.OrderRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@Builder
@RequiredArgsConstructor
@Transactional()
public class OrderServicImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public void PurchaseFromCart(Long memberId) {
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);


        if(cartItems.isEmpty()){
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        Order order = Order.builder()
                .memberId(memberId)
                .orderDate(LocalDateTime.now())
                .status(true)
                .build();

        for(CartItem cart : cartItems){
            Product product = cart.getProduct();


            OrderItem orderItem = OrderItem.builder()
                    .productId(product.getProductId())
                    .quantity(cart.getQuantity())
                    .price(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                    .build();

            order.addOrderItem(orderItem);
        }
        // 4. 주문 저장
        orderRepository.save(order);

        // 5. 장바구니 비우기
//        cartRepository.deleteByMemberId(memberId);

    }
}
