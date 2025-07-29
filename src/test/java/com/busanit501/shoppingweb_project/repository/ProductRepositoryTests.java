package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.Review;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
public class ProductRepositoryTests {

    // ProductRepository 주입
    // 이유: 테스트용 상품 데이터를 조회하거나 생성하기 위해 필요합니다.
    @Autowired
    private ProductRepository productRepository;

    // ReviewRepository 주입
    // 이유: 테스트용 리뷰 데이터를 생성하고 DB에 저장하기 위해 필요합니다.
    @Autowired
    private ReviewRepository reviewRepository;

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

    // 테스트용 리뷰 데이터를 생성하는 테스트 메소드
    // 이유: 댓글 페이징 기능이 화면에 정상적으로 보이는지 확인하려면,
    //      DB에 충분한 양의 댓글 데이터가 미리 존재해야 합니다.
    //      이 테스트를 한번 실행하면, 1번 상품에 20개의 댓글이 자동으로 생성됩니다.
    @Test
    @Transactional // Product와 Review를 함께 다루므로 트랜잭션 처리가 안전합니다.
    public void insertReviewsTest() {
        // 1번 상품을 대상으로 리뷰를 작성합니다.
        Long targetProductId = 1L;

        // findById를 통해 실제 DB에 존재하는 Product 객체를 가져옵니다.
        // orElseThrow: 만약 1번 상품이 없다면 테스트를 즉시 실패시킵니다.
        Product product = productRepository.findById(targetProductId)
                .orElseThrow(() -> new IllegalArgumentException("테스트할 상품이 DB에 없습니다. insertProductsTest를 먼저 실행해주세요."));

        // 20개의 테스트 리뷰를 반복문으로 생성합니다.
        IntStream.rangeClosed(1, 20).forEach(i -> {
            // @Builder를 사용하여 Review 객체를 생성합니다.
            // 이유: @Setter를 사용하지 않고, 객체의 불변성을 유지하면서
            //      안전하게 객체를 생성하고 초기화할 수 있는 가장 좋은 방법입니다.
            Review review = Review.builder()
                    .reviewContent("테스트 리뷰 내용입니다..." + i)
                    .rating((int)(Math.random() * 5) + 1) // 1~5점 랜덤 평점
                    .product(product) // 연관관계 설정
                    .build();

            reviewRepository.save(review);
        });
    }
}