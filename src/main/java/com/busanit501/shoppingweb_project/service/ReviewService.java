package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ReviewDTO;

public interface ReviewService {
    /**
     * [lsr/feature/paging] 리뷰 페이징 목록 기능 추가
     * 특정 상품에 대한 리뷰 목록을 페이징 처리하여 반환합니다.
     *
     * @param productId      상품 ID
     * @param pageRequestDTO 페이징 요청 정보
     * @return 페이징된 리뷰 DTO 목록
     */
    PageResponseDTO<ReviewDTO> getReviewList(Long productId, PageRequestDTO pageRequestDTO);

    /**
     * [busanit] 리뷰 등록 기능 (구매자만 가능)
     * 
     * @param reviewDTO 등록할 리뷰 정보
     * @param memberId  작성자 ID
     * @return 등록된 리뷰의 ID
     */
    Long registerReview(ReviewDTO reviewDTO, String memberId);

    /**
     * [busanit] 사용자가 특정 상품을 구매했는지 확인
     * 
     * @param memberId  사용자 ID
     * @param productId 상품 ID
     * @return 구매 여부 (true/false)
     */
    boolean checkIfUserPurchasedProduct(String memberId, Long productId);
}