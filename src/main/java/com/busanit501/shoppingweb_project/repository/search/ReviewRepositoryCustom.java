package com.busanit501.shoppingweb_project.repository.search;

import com.busanit501.shoppingweb_project.domain.Review;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    /**
     * 특정 상품에 달린 댓글 목록을 페이징하여 조회합니다.
     * @param productId 상품 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    Page<Review> getReviewsByProductId(Long productId, Pageable pageable);

}