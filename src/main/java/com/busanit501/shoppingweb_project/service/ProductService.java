package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.ProductImage;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
     * [lsr/feature/paging] 페이징 기능과 공존하는 전체 조회 기능
     * - 페이징: 사용자에게 보여주는 메인 상품 목록용 (성능)
     * - 전체조회: 관리자 페이지 통계 등 내부적인 용도
     * ==================================================
     */
    // 전체 목록 조회
    List<ProductDTO> getAllProducts();

    // 카테고리별 조회
    List<ProductDTO> getProductsByCategory(String category);

    // 키워드 검색
    List<ProductDTO> searchProducts(String keyword);

    void saveProduct(Product product);

    // 새 상품 등록 메서드 시그니처 추가
    ProductDTO createProduct(ProductDTO productDTO);

    // 상품 수정 메서드 시그니처 추가
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    // 상품 삭제 메서드 시그니처 추가
    void deleteProduct(Long productId);

    // 관리자 모드에서 상품과 이미지 함께 저장
    void createProductWithImages(String productName, BigDecimal price, int stock, ProductCategory productTag,
            MultipartFile thumbnail,
            List<MultipartFile> detailImages);
}
