package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    // 단건 조회
    ProductDTO getProductById(Long productId);

    // 전체 목록 조회
    List<ProductDTO> getAllProducts();

    // 카테고리별 조회
    List<ProductDTO> getProductsByCategory(String category);

    // 키워드 검색
    List<ProductDTO> searchProducts(String keyword);

    default Product dtoToEntity(ProductDTO productDTO){
        Product product = Product.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .build();
        return product;
    }
    default ProductDTO entityToDto(Product product){
        ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        return productDTO;
    }
}
