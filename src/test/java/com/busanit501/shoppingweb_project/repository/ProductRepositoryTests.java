package com.busanit501.shoppingweb_project.repository;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.ProductImage;
import com.busanit501.shoppingweb_project.domain.Review;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
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
    @Transactional
    @Commit
    public void insertProductsAndReviewsTest() {
        // 카테고리별 아이템 목록 정의
        Map<ProductCategory, String[]> itemsByCategory = new HashMap<>();
        itemsByCategory.put(ProductCategory.TOP, new String[] { "티셔츠", "남방", "가디건" });
        itemsByCategory.put(ProductCategory.BOTTOM, new String[] { "바지", "스키니 진", "청바지" });
        itemsByCategory.put(ProductCategory.OUTER, new String[] { "자켓", "코트", "바람막이" });
        itemsByCategory.put(ProductCategory.DRESS, new String[] { "원피스", "드레스" });
        itemsByCategory.put(ProductCategory.SHOES, new String[] { "신발", "운동화", "구두" });
        itemsByCategory.put(ProductCategory.BAG, new String[] { "가방", "백팩", "핸드백" });
        itemsByCategory.put(ProductCategory.ACC, new String[] { "액세서리", "모자", "벨트" });
        itemsByCategory.put(ProductCategory.UNKNOWN, new String[] { "손난로", "특이한 아이템" });

        String[] seasons = { "봄", "여름", "가을", "겨울" };
        String[] colors = { "레드", "블루", "그린", "블랙", "화이트", "핑크", "옐로우" };
        String[] features = { "오버핏", "슬림핏", "방수", "경량", "기모", "하와이안" };

        ProductCategory[] categories = ProductCategory.values();
        Random random = new Random();
 
        // 샘플 이미지 파일 목록 (src/main/resources/static/images/products/ 에 위치해야 함)
        List<String> sampleImageFiles = IntStream.rangeClosed(1, 100)
                .mapToObj(num -> String.format("sample%03d.jpg", num))
                .collect(Collectors.toList());
 
         // 테스트 실행 시, 상품 데이터 100개와 각 상품별 리뷰를 DB에 자동으로 추가합니다.
         IntStream.rangeClosed(1, 100).forEach(i -> {
            ProductCategory currentCategory = categories[i % categories.length];
            String[] possibleItems = itemsByCategory.get(currentCategory);

            String season = seasons[random.nextInt(seasons.length)];
            String itemType = possibleItems[random.nextInt(possibleItems.length)];
            String color = colors[random.nextInt(colors.length)];
            String feature = features[random.nextInt(features.length)];
            String productName = String.format("[%s] %s %s %s", season, color, feature, itemType);

            Product product = Product.builder()
                    .productName(productName)
                    .price(BigDecimal.valueOf(10000 + (random.nextInt(500) * 100)))
                    .stock(random.nextInt(101) + 50)
                    .productTag(currentCategory)
                    .build();
            productRepository.save(product);
 
            // --- 이미지 생성 로직 추가 ---
            // 각 상품에 대해 3개의 테스트 이미지를 생성합니다.
            for (int j = 0; j < 3; j++) {
                // 첫 번째 이미지를 썸네일로 설정합니다. (j == 0)
                boolean isThumbnail = (j == 0);

                // ProductImage 객체를 생성하고, 파일 이름과 순서, 썸네일 여부를 설정합니다.
                String randomImageName = sampleImageFiles.get(random.nextInt(sampleImageFiles.size()));
                ProductImage productImage = ProductImage.builder()
                        .fileName(randomImageName)
                        .ord(j)
                        .thumbnail(isThumbnail)
                        .build();

                // Product 객체에 생성된 이미지를 추가합니다.
                product.addImage(productImage);
            }
            productRepository.save(product);

 
             // --- 리뷰 생성 로직 추가 ---
             int reviewCount = random.nextInt(5) + 1; // 상품당 1~5개의 리뷰를 랜덤으로 생성
            IntStream.rangeClosed(1, reviewCount).forEach(j -> {
                Review review = Review.builder()
                        .reviewContent(product.getProductName() + "에 대한 테스트 리뷰입니다..." + j)
                        .rating(random.nextInt(5) + 1) // 1~5점 랜덤 평점
                        .product(product) // 방금 만든 상품과 연결
                        .build();
                reviewRepository.save(review);
            });
        });
    }

    // 테스트용 리뷰 데이터를 생성하는 테스트 메소드
    // 이유: 댓글 페이징 기능이 화면에 정상적으로 보이는지 확인하려면,
    // DB에 충분한 양의 댓글 데이터가 미리 존재해야 합니다.
    // 이 테스트를 한번 실행하면, 2번 상품에 40개의 댓글이 자동으로 생성됩니다.
    @Test
    @Transactional
    @Commit // 테스트가 완료된 후 트랜잭션을 롤백하지 않고 커밋하도록 설정합니다.
    public void insertReviewsTest() {
        // 2번 상품을 대상으로 리뷰를 작성합니다. (번호 교체 가능)
        Long targetProductId = 2L;

        // findById를 통해 실제 DB에 존재하는 Product 객체를 가져옵니다.
        // orElseThrow: 만약 해당 번호 상품이 없다면 테스트를 즉시 실패시킵니다.
        Product product = productRepository.findById(targetProductId)
                .orElseThrow(() -> new IllegalArgumentException("테스트할 상품이 DB에 없습니다. insertProductsTest를 먼저 실행해주세요."));

        // 40개의 테스트 리뷰를 반복문으로 생성합니다.
        IntStream.rangeClosed(1, 40).forEach(i -> {
            // @Builder를 사용하여 Review 객체를 생성합니다.
            // 이유: @Setter를 사용하지 않고, 객체의 불변성을 유지하면서
            // 안전하게 객체를 생성하고 초기화할 수 있는 가장 좋은 방법입니다.
            Review review = Review.builder()
                    .reviewContent("테스트 리뷰 내용입니다..." + i)
                    .rating((int) (Math.random() * 5) + 1) // 1~5점 랜덤 평점
                    .product(product) // 연관관계 설정
                    .build();

            reviewRepository.save(review);
        });
    }
}