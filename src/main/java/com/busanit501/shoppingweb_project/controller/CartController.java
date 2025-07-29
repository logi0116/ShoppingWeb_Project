package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.dto.CartItemDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.service.OrderService;
import com.busanit501.shoppingweb_project.service.ProductService;
import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {
    private final OrderService orderService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody CartItemDTO dto) {
        CartItemDTO saved = orderService.AddCartItemFromProductDetail(dto);
        return ResponseEntity.ok(saved);
    }
}
