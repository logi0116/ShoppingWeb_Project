package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    /**
     * [lsr/feature/paging] 상품 목록/검색 페이징 기능
     * 
     * @param pageRequestDTO 페이징 및 검색 조건
     * @return 페이징된 상품 목록
     */
    PageResponseDTO<ProductDTO> getProductList(PageRequestDTO pageRequestDTO);

    // 단건 조회
    ProductDTO getProductById(Long productId);

    /*
     * ==================================================
     * [lsr/feature/paging] 기존 코드 보존 (주석 처리)
     * - 이유: 아래 메소드들은 페이징 기능이 없는 버전이며, 새로운 getProductList 메소드로 통합되었습니다.
     * ==================================================
     */
    // // 전체 목록 조회
    // List<ProductDTO> getAllProducts();
    //
    // // 카테고리별 조회
    // List<ProductDTO> getProductsByCategory(String category);
    //
    // // 키워드 검색
    // List<ProductDTO> searchProducts(String keyword);

    void saveProduct(Product product);

    default Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .image(productDTO.getImage())
                .productTag(productDTO.getProductTag())
                .build();
        return product;
    }

    default ProductDTO entityToDto(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .stock(product.getStock())
                .image(product.getImage())
                .productTag(product.getProductTag())
                .build();
        return productDTO;
    }

    // 새 상품 등록 메서드 시그니처 추가
    ProductDTO createProduct(ProductDTO productDTO);

    // 상품 수정 메서드 시그니처 추가
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    // 상품 삭제 메서드 시그니처 추가
    void deleteProduct(Long productId);
}
