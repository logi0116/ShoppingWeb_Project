package com.busanit501.shoppingweb_project.repository.search;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.QProduct;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
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
                conditionBuilder.or(product.productName.contains(keyword));
            }
            if (type.contains("t")) { // 상품 태그 (productTag)
                conditionBuilder.or(product.productTag.stringValue().contains(keyword));
            }

            booleanBuilder.and(conditionBuilder);
        }
        query.where(booleanBuilder);

        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        List<Product> content = query.fetch();

        // fetchCount() 대신 count 쿼리를 분리하여 실행
        JPAQuery<Long> countQuery = queryFactory.select(product.count())
                .from(product)
                .where(booleanBuilder);

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}