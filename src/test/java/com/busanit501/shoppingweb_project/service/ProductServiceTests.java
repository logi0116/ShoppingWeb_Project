package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest // 스프링 컨텍스트를 로드해서 테스트
@Log4j2 // 로그 출력용
public class ProductServiceTests {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testProductInsert() {
        // 상품 등록 테스트
        Product product = Product.builder()
                .productName("상품등록 테스트")
                .price(BigDecimal.valueOf(100000))
                .stock(50)
                .productTag(ProductCategory.TOP)
                .build();

        // 서비스 호출로 저장
        productService.saveProduct(product);

        // 로그 출력
        log.info("상품명(Name) : " + product.getProductName());
        log.info("가격(Price) : " + product.getPrice());
        log.info("재고(Stock) : " + product.getStock());
        log.info("카테고리(Tag) : " + product.getProductTag());
    }

    @Test
    public void testSelectProduct() {
        // 상품 조회 테스트
        Long productId = 9L; // 조회하고 싶은 상품 ID

        // Repository에서 특정 상품 ID로 조회
        Optional<Product> result = productRepository.findById(productId);

        // 값이 없으면 예외 발생, 있으면 Product 객체 반환
        Product product = result.orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다."));

        // 결과 로그 출력
        log.info("상품명 (Name) : " + product.getProductName());
        log.info("가격 (Price) : " + product.getPrice());
        log.info("재고 (Stock) : " + product.getStock());
        log.info("카테고리 (Tag) : " + product.getProductTag());
    }

    @Test
    public void testUpdateProducts() {
        // 상품 수정 테스트
        Long productId = 9L; // 수정할 상품의 ID

        // 상품을 ID로 조회 (Optional로 감싸서 예외 처리)
        Optional<Product> result = productRepository.findById(productId);

        // 없으면 예외 발생
        Product product = result.orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // 수정 전 정보 로그
        log.info("상품명 (Name) : " + product.getProductName());
        log.info("가격 (Price) : " + product.getPrice());
        log.info("재고 (Stock) : " + product.getStock());
        log.info("카테고리 (Tag) : " + product.getProductTag());

        ProductDTO productDTO = ProductDTO.builder()
                .productName("테스트")
                .price(BigDecimal.valueOf(5000))
                .stock(20)
                .productTag(ProductCategory.OUTER)
                .build();
        // 상품 내용 수정 (아래 메서드는 직접 구현해야 합니다)
        product.changeTitleContent(productDTO);

        // 수정된 상품 저장 (JPA가 update 쿼리 자동 실행)
        productRepository.save(product);

        // 수정 후 정보 로그
        log.info("상품명 (Name) : " + product.getProductName());
        log.info("가격 (Price) : " + product.getPrice());
        log.info("재고 (Stock) : " + product.getStock());
        log.info("카테고리 (Tag) : " + product.getProductTag());
    }

    @Test
    public void testDeleteProduct() {
        Long productId = 1L; // 삭제할 상품 ID

        // 삭제 전 존재 여부 확인 (Optional로 안전하게 처리)
        Optional<Product> result = productRepository.findById(productId);

        if (result.isPresent()) {
            productRepository.deleteById(productId); // 상품 삭제
            log.info("상품이 성공적으로 삭제되었습니다. ID: " + productId);
            log.info("삭제된 상품이름. Name: " + result.get().getProductName());
        } else {
            log.warn("삭제하려는 상품이 존재하지 않습니다. ID: " + productId);
        }
    }
}


