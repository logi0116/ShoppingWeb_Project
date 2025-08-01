package com.busanit501.shoppingweb_project.repository.search;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.QProduct;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Product> search(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("productId");
        QProduct product = QProduct.product;

        JPAQuery<Product> query = queryFactory.selectFrom(product);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        String type = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();

        if (StringUtils.hasText(keyword) && StringUtils.hasText(type)) {
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            if (type.contains("n")) { // 상품명 (productName)
                conditionBuilder.or(product.productName.containsIgnoreCase(keyword));
            }
            if (type.contains("t")) { // 상품 태그 (productTag)
                try {
                    ProductCategory categoryEnum = ProductCategory.fromKoreanName(keyword);
                    conditionBuilder.or(product.productTag.eq(categoryEnum));
                } catch (IllegalArgumentException e) {
                    // 변환에 실패하면 (예: '하의'가 아닌 일반 검색어) 무시하고 넘어감
                }
            }
            // 다른 검색 조건(예: 내용 'c')이 필요하면 여기에 추가할 수 있습니다.

            booleanBuilder.and(conditionBuilder);
        }
        query.where(booleanBuilder);

        // 페이징 적용
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        List<Product> content = query.fetch();

        // 전체 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(product.count())
                .from(product)
                .where(booleanBuilder);

        Long total = countQuery.fetchOne(); // 결과가 null일 수 있으므로 Long 타입으로 받습니다.

        // total이 null이면 0L(0 long)을, 아니면 실제 total 값을 사용합니다.
        return new PageImpl<>(content, pageable, (total != null) ? total : 0L);
    }
}