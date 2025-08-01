package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.*;
import com.busanit501.shoppingweb_project.dto.*;
import com.busanit501.shoppingweb_project.repository.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Builder
@RequiredArgsConstructor
@Transactional()
public class OrderServicImpl implements OrderService {

    private final ModelMapper modelMapper;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartRepository;
    private final MemberRepository memberRepository;
    private final AddressRepository addressRepository;

    @Override
    public OrderDTO PurchaseFromCart(Long memberId) {
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("OrderServcie에서 작업중 member객체에 null값이 있습니다."));

        Address address = addressRepository.findByMemberAndIsDefaultTrue(member);
        UserinfoDTO userinfoDTo = UserinfoDTO.toUserinfoDTO(member, address);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        // 총합 계산

        BigDecimal total = BigDecimal.ZERO;

        // 주문 객체 생성
        Order order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .status(true) // 또는 enum 사용 시 OrderStatus.ORDERED
                .address(userinfoDTo.getAddressId())
                .addressDetail(userinfoDTo.getAddressLine())
                .receiverName(userinfoDTo.getUserName())
                .receiverPhone(userinfoDTo.getPhone())
                .build();

        for (CartItem cart : cartItems) {
            Product product = cart.getProduct();
            product.removeStock(cart.getQuantity());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            total = total.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cart.getQuantity())
                    .price(itemTotal)
                    .build();

            order.addOrderItem(orderItem); // 연관관계 설정
        }

        order.setTotalPrice(total.intValue()); // Order에 총합 저장

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 장바구니 비우기
        cartItemRepository.deleteByMemberId(memberId);

        // DTO 매핑은 저장 후!
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public CartItemDTO AddCartItemFromProductDetail(CartItemDTO cartItemDTO) {
        CartItem cartItem = new ModelMapper().map(cartItemDTO, CartItem.class);

        cartItemRepository.save(cartItem);
        return cartItemDTO;
    }

    @Override
    public List<OrderDTO> getOrderHistoryByMemberId(Long memberId) {
        log.info("OrderService에서 작업중 넘어온 memberId : " + memberId);
        // memberId는 Long 타입이지만, Member 엔티티의 식별자인 mid는 String일 수 있으므로,
        // 레포지토리에서 mid를 사용한다고 가정하고, memberId를 String으로 변환하여 전달합니다.
        // 만약 Member의 PK가 Long 타입 id라면, findByMember_Id(memberId)를 사용해야 합니다.
        // 여기서는 findByMember_Mid를 사용하도록 수정합니다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        List<Order> orders = orderRepository.findByMember(member);
        List<OrderDTO> orderDTOList = orders.stream().map(order -> {
            OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(orderItem -> {
                        OrderItemDTO dto = modelMapper.map(orderItem, OrderItemDTO.class);

                        // 🔽 연관관계 매핑으로 Product를 직접 가져올 수 있으므로 DB 조회 불필요
                        if (orderItem.getProduct() != null) {
                            dto.setProductName(orderItem.getProduct().getProductName());
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            orderDTO.setOrderItems(orderItemDTOs);
            return orderDTO;
        }).collect(Collectors.toList());
        log.info("OrderService에서 작업중 orderDTO : " + orderDTOList);
        return orderDTOList;
    }
}
