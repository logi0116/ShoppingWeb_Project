package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void insertProductsTest() {
        // 검색 테스트를 위한 다양한 키워드 배열
        String[] seasons = { "봄", "여름", "가을", "겨울" };
        String[] itemTypes = { "티셔츠", "바지", "자켓", "원피스", "신발", "가방", "액세서리", "남방", "가디건", "손난로" };
        String[] colors = { "레드", "블루", "그린", "블랙", "화이트", "핑크", "옐로우" };
        String[] features = { "오버핏", "슬림핏", "방수", "경량", "기모", "하와이안" };

        ProductCategory[] categories = {
                ProductCategory.TOP, ProductCategory.BOTTOM, ProductCategory.OUTER,
                ProductCategory.DRESS, ProductCategory.SHOES, ProductCategory.BAG,
                ProductCategory.ACC, ProductCategory.UNKNOWN
        };

        Random random = new Random();

        // 테스트 실행 시, 상품 데이터 100개를 DB에 자동으로 추가합니다.
        IntStream.rangeClosed(1, 100).forEach(i -> {
            // 키워드 랜덤 조합
            String season = seasons[random.nextInt(seasons.length)];
            String itemType = itemTypes[random.nextInt(itemTypes.length)];
            String color = colors[random.nextInt(colors.length)];
            String feature = features[random.nextInt(features.length)];

            // 상품명과 설명 생성
            String productName = String.format("[%s] %s %s %s", season, color, feature, itemType);
            Product product = Product.builder()
                    .productName(productName)
                    .price(BigDecimal.valueOf(10000 + (random.nextInt(500) * 100)))
                    .stock(random.nextInt(101) + 50) // 50 ~ 150
                    .productTag(categories[i % categories.length])
                    .build();
            productRepository.save(product);
        });
    }
}