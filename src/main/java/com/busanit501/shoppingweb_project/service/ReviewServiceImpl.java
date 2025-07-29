package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Review;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ReviewDTO;
import com.busanit501.shoppingweb_project.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    // 의존성 주입: ReviewRepository를 사용하여 DB와 통신합니다.
    private final ReviewRepository reviewRepository;

    /**
     * ReviewService 인터페이스의 getListOfReviews 메소드를 실제로 구현하는 부분입니다.
     * 역할: Controller로부터 받은 요청(productId, pageRequestDTO)을 바탕으로,
     *      Repository를 호출하여 DB에서 데이터를 가져오고,
     *      가져온 데이터를 View가 사용하기 좋은 PageResponseDTO 형태로 가공하여 반환합니다.
     */
    @Override
    public PageResponseDTO<ReviewDTO> getListOfReviews(Long productId, PageRequestDTO pageRequestDTO) {
        
        // 1. Pageable 객체 생성
        // 이유: Repository에 페이징 및 정렬 정보를 전달하기 위한 표준 규격 객체입니다.
        // "reviewId"를 기준으로 내림차순(최신순) 정렬하도록 설정합니다.
        Pageable pageable = pageRequestDTO.getPageable("reviewId");

        // 2. Repository 호출
        // 상호작용: 우리가 만든 ReviewRepositoryCustomImpl의 getReviewsByProductId 메소드가 호출됩니다.
        // 결과: 특정 상품에 해당하는 Review Entity 목록이 페이징된 형태로 Page<Review> 객체에 담겨 반환됩니다.
        Page<Review> result = reviewRepository.getReviewsByProductId(productId, pageable);

        // 3. Entity 목록을 DTO 목록으로 변환
        // 이유: Entity(DB 데이터)를 Controller나 View 같은 프레젠테이션 계층에 직접 노출하는 것은 좋지 않은 설계입니다.
        //      필요한 데이터만 담은 DTO로 변환하여 전달하는 것이 안전하고 유연합니다.
        List<ReviewDTO> dtoList = result.getContent().stream()
                .map(review -> entityToDto(review)) // 아래에 만든 변환 메소드 사용
                .collect(Collectors.toList());

        // 4. 최종 응답 DTO 생성
        // 상호작용: 우리가 만든 PageResponseDTO를 사용하여, 최종 결과를 포장합니다.
        //          이 객체는 Controller를 통해 View(Javascript)로 전달되어 페이지네이션 UI를 그리는 데 사용됩니다.
        return PageResponseDTO.<ReviewDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    /**
     * Entity를 DTO로 변환하는 헬퍼 메소드입니다.
     * 이유: ModelMapper 라이브러리 없이 직접 변환 로직을 구현하기로 결정했기 때문에,
     *      변환 과정을 이 메소드에 캡슐화하여 코드의 중복을 피하고 재사용성을 높입니다.
     * @param review 변환할 Review Entity
     * @return 변환된 ReviewDTO
     */
    private ReviewDTO entityToDto(Review review) {
        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .reviewContent(review.getReviewContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .build();
    }
}