package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.domain.CartItem;
import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.dto.CartItemDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.repository.CartItemRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import com.busanit501.shoppingweb_project.service.CartItemService;
import com.busanit501.shoppingweb_project.security.CustomUserDetails;
import com.busanit501.shoppingweb_project.service.OrderService;
import com.busanit501.shoppingweb_project.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {
    private final OrderService orderService;
    private final ProductService productService;
    private final CartItemService cartItemService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        log.info("CartController에서 작업중 memberId : " + memberId);
        return ResponseEntity.ok(cartItemService.getCartItemsByMemberId(memberId));
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody CartItemDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("화면에서 받아온 dto확인중 : " + dto);
        Long memberId = userDetails.getMemberId();
        dto.setMemberId(memberId);

        CartItemDTO saved = cartItemService.addToCart(dto);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long productId,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            int quantityChange = request.get("quantityChange");
            Long memberId = userDetails.getMemberId();

            CartItemDTO updated = cartItemService.updateQuantity(memberId, productId, quantityChange);
            log.info("CartController에서 작업중 업데이트된 CartItemDTO : " + updated);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartItemService.removeFromCart(userDetails.getMemberId(), productId);
        Map<String, String> result = new HashMap<>();
        result.put("message", "삭제 성공");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartItemService.clearCart(userDetails.getMemberId());
        Map<String, String> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCartItemCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Long> response = new HashMap<>();
        if (userDetails == null) {
            response.put("count", 0L);
            return ResponseEntity.ok(response);
        }
        Long memberId = userDetails.getMemberId();
        long count = cartItemService.getCartItemCountByMemberId(memberId);
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
