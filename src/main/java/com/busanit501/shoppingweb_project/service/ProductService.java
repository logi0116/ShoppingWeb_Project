package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import com.busanit501.shoppingweb_project.dto.ProductRequestDto;
import com.busanit501.shoppingweb_project.dto.ProductResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product();
        product.setProductName(requestDto.getProductName());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());
        product.setProductTag(ProductCategory.fromKoreanName(requestDto.getProductTag()));

        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct);
    }

    public List<ProductResponseDto> getAllProducts() {
        List<ProductResponseDto> products = productRepository.findAll().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
        log.info("ProductService에서 products 확인 : "+products);
        return products;
    }

    public ProductResponseDto getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. 상품 ID: " + productId));
        return new ProductResponseDto(product);
    }


    public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. 상품 ID: " + productId));

        product.setProductName(requestDto.getProductName());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());
        product.setProductTag(ProductCategory.fromKoreanName(requestDto.getProductTag()));

        return new ProductResponseDto(product);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("삭제할 상품을 찾을 수 없습니다. 상품 ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

}