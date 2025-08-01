package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.dto.CartItemDTO;

import java.util.List;

public interface CartItemService {
    CartItemDTO addToCart(CartItemDTO cartItemDTO);

    List<CartItemDTO> getCartItems();

    CartItemDTO updateQuantity(Long memberId, Long productId, int change);

    void deleteCartItem(Long productId);

    void clearCart(Long id);

    void removeFromCart(Long memberId, Long productId);

    List<CartItemDTO> getCartItemsByMemberId(Long memberId);

    long getCartItemCountByMemberId(Long memberId);
}
