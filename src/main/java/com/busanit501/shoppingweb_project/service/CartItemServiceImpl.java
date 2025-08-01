package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.CartItem;
import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.dto.CartItemDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.repository.CartItemRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import com.busanit501.shoppingweb_project.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CartItemServiceImpl implements CartItemService {
    private final ModelMapper modelMapper;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    ProductService productService;

    private Long getCurrentMemberId() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getMemberId();
    }

    // 장바구니 조회
    public List<CartItemDTO> getCartItems() {
        Long memberId = getCurrentMemberId();
        List<CartItem> items = cartItemRepository.findByMemberId(memberId);
        return items.stream()
                .map(cartItem -> {
                    ProductDTO productDTO = productService.getProductById(cartItem.getProduct().getProductId());
                    return CartItemDTO.fromEntity(cartItem, productDTO);
                })
                .collect(Collectors.toList());
    }

    // 장바구니 추가
    public CartItemDTO addToCart(CartItemDTO dto) {
        Long memberId = getCurrentMemberId();
        Product product = productRepository.findByProductId(dto.getProductId());
        ProductDTO productDTO = productService.getProductById(dto.getProductId());
        Optional<CartItem> existing = cartItemRepository.findByMemberIdAndProduct(memberId, product);
        CartItem cartItem;

        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.increaseQuantity(dto.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .memberId(memberId)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .build();
        }
        cartItemRepository.save(cartItem);
        return CartItemDTO.fromEntity(cartItem, productDTO);
    }

    // 수량 변경
    @Transactional
    public CartItemDTO updateQuantity(Long memberId, Long productId, int change) {
        Product product = productRepository.findByProductId(productId);
        ProductDTO productDTO = productService.getProductById(productId);

        CartItem item = cartItemRepository.findByMemberIdAndProduct(memberId, product)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));

        // 재고 확인 로직: 수량을 늘릴 때(change > 0), 현재 수량과 더하려는 수량의 합이 재고보다 큰지 확인합니다.
        if (change > 0 && item.getQuantity() + change > product.getStock()) {
            // 재고가 부족하면 예외를 발생시켜 요청을 중단시킵니다.
            throw new IllegalStateException("재고가 부족합니다.");
        }

        log.info("CartController에서 작업중 업데이트되기 전 CartItemDTO : " + item);
        item.increaseQuantity(change);
        if (item.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        }
        return CartItemDTO.fromEntity(item, productDTO);
    }

    // 회원 ID로 장바구니 아이템 조회
    public List<CartItemDTO> getCartItemsByMemberId(Long memberId) {
        List<CartItem> cartItems = cartItemRepository.findByMemberId(memberId);
        log.info("CartItemService작업 중 Id : " + memberId + " Product의 갯수 : " + cartItems.toArray().length);
        return cartItems.stream().map(cartItem -> {
            ProductDTO productDTO = productService.getProductById(cartItem.getProduct().getProductId());
            return CartItemDTO.fromEntity(cartItem, productDTO);
        }).collect(Collectors.toList());
    }

    // 장바구니에서 특정 상품 제거 (memberId와 productId로)
    public void removeFromCart(Long memberId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
        log.info("CartItemService에서 작업중 삭제한 product : " + product.getProductName());
        Optional<CartItem> cartItem = cartItemRepository.findByMemberIdAndProduct(memberId, product);
        cartItem.ifPresent(cartItemRepository::delete);
        // cartItem.ifPresent(cartItem -> {
        // cartItemRepository.delete(cartItem);
        // }); 이거랑 똑같은 기능
    }

    // 단일 삭제
    public void deleteCartItem(Long productId) {
        Long memberId = getCurrentMemberId();
        Product product = productRepository.findByProductId(productId);
        cartItemRepository.deleteByMemberIdAndProduct(memberId, product);
    }

    // 전체 삭제
    public void clearCart(Long id) {
        Long memberId = getCurrentMemberId();
        cartItemRepository.deleteByMemberId(memberId);
    }

    @Override
    public long getCartItemCountByMemberId(Long memberId) {
        return cartItemRepository.countByMemberId(memberId);
    }
}
