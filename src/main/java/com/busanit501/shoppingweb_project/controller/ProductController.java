package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.service.ProductService;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;

    /**
     * 페이징 처리된 상품 목록을 반환합니다. (메인 페이지용)
     * 프론트엔드의 home-paging.js가 이 API를 호출합니다.
     * 주소: /api/products?page=1&size=10...
     * 
     * @param pageRequestDTO page, size, type, keyword 등의 조건을 담습니다.
     * @return 페이징된 상품 데이터
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductDTO>> getProductList(PageRequestDTO pageRequestDTO) {
        log.info("getProductList (paging) 호출: " + pageRequestDTO);
        PageResponseDTO<ProductDTO> responseDTO = productService.getProductList(pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 전체 상품 목록을 반환합니다. (내부 관리용 또는 페이징이 필요 없는 경우)
     * 주소: /api/products/all
     * 
     * @return 전체 상품 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("getAllProducts (no paging) 호출");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /*
     * [기존 코드 설명]
     * 아래의 searchProducts와 기존 getAllProducts(category 처리 로직 포함)는
     * 새로운 getProductList(PageRequestDTO) 방식으로 모두 통합되었습니다.
     * 따라서 아래 코드들은 더 이상 필요하지 않습니다.
     */
    // @GetMapping("/search")
    // public List<ProductDTO> searchProducts(@RequestParam String keyword) {
    // List<ProductDTO> products = productService.searchProducts(keyword);
    // log.info(keyword + "가 포함된 데이터 : "+keyword);
    // return products;
    // }

    // @GetMapping("/{productId}")
    // public ProductDTO getProductById(@PathVariable Long productId){
    // // @PathVariable => URL에 포함된 변수를 메서드 파라미터로 매핑해주는 어노테이션
    // return productService.getProductById(productId);
    //// productService.getProductById(productId) => productDTO
    // }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam String productName,
            @RequestParam BigDecimal price,
            @RequestParam int stock,
            @RequestParam ProductCategory productTag,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "details", required = false) List<MultipartFile> details) {

        productService.createProductWithImages(productName, price, stock, productTag, thumbnail, details);
        return ResponseEntity.ok().build();
    }

    // @GetMapping
    // public ResponseEntity<List<ProductDTO>> getAllProducts() {
    // List<ProductDTO> products = productService.getAllProducts();
    // return ResponseEntity.ok(products);
    // }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return ResponseEntity.ok(productDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO requestDto) {
        log.info("ProductControllerRestAPI에서 작업중 화면에서 가져온 데이터 확인중 : productId " + productId + "productDTO : "
                + requestDto.getProductName());
        ProductDTO updatedProduct = productService.updateProduct(productId, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

}