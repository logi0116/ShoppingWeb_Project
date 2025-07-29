package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

@SpringBootTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void insertProductsTest() {
        // 실제 정의된 카테고리들을 배열로 준비 (UNKNOWN 제외)
        ProductCategory[] categories = {
                ProductCategory.TOP,
                ProductCategory.BOTTOM,
                ProductCategory.OUTER,
                ProductCategory.DRESS,
                ProductCategory.SHOES,
                ProductCategory.BAG,
                ProductCategory.ACC
        };

        // 테스트 실행 시, 상품 데이터 100개를 DB에 자동으로 추가합니다.
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Product product = Product.builder()
                    .productName("테스트 상품 " + i)
                    .price(BigDecimal.valueOf(10000 + (i * 100)))
                    .stock(100)
                    // 나머지 연산(%)을 이용해 카테고리 배열을 순환하며 적용
                    .productTag(categories[i % categories.length])
                    .build();
            productRepository.save(product);
        });
    }
}