package com.busanit501.shoppingweb_project.repository.search;

import com.busanit501.shoppingweb_project.domain.QReview;
import com.busanit501.shoppingweb_project.domain.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 생성자를 통해 JPAQueryFactory를 주입받습니다.
    // 이유: Querydsl을 사용하여 동적 쿼리를 생성하고 실행하기 위한 핵심 객체입니다.
    public ReviewRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Review> getReviewsByProductId(Long productId, Pageable pageable) {
        QReview review = QReview.review;

        // select from review where product_id = ? order by createdAt desc limit ?, ?
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(review.product.productId.eq(productId))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // select count(review) from review where product_id = ?
        long total = queryFactory
                .select(review.count())
                .from(review)
                .where(review.product.productId.eq(productId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}