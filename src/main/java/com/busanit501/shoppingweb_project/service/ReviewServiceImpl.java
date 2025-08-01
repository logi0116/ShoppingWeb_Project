package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Member;
import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.Review;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ReviewDTO;
import com.busanit501.shoppingweb_project.repository.MemberRepository;
import com.busanit501.shoppingweb_project.repository.OrderItemRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import com.busanit501.shoppingweb_project.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

        private final ReviewRepository reviewRepository;
        private final ModelMapper modelMapper;
        private final OrderItemRepository orderItemRepository;
        private final MemberRepository memberRepository;
        private final ProductRepository productRepository;

        @Override
        public PageResponseDTO<ReviewDTO> getReviewList(Long productId, PageRequestDTO pageRequestDTO) {
                Page<Review> result = reviewRepository.getReviewsByProductId(productId, pageRequestDTO);

                List<ReviewDTO> dtoList = result.getContent().stream()
                                .map(this::entityToDto)
                                .collect(Collectors.toList());

                return PageResponseDTO.<ReviewDTO>withAll()
                                .pageRequestDTO(pageRequestDTO)
                                .dtoList(dtoList)
                                .totalCount(result.getTotalElements())
                                .build();
        }

        @Override
        public Long registerReview(ReviewDTO reviewDTO, String memberId) {
                boolean hasPurchased = orderItemRepository.existsByMemberAndProduct(
                                memberId,
                                reviewDTO.getProductId());

                if (!hasPurchased) {
                        throw new IllegalStateException("이 상품을 구매한 사용자만 리뷰를 작성할 수 있습니다.");
                }

                Product product = productRepository.findById(reviewDTO.getProductId())
                                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                Member member = memberRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                Review review = Review.builder()
                                .reviewContent(reviewDTO.getReviewContent())
                                .rating(reviewDTO.getRating())
                                .product(product)
                                .member(member)
                                .build();

                Review savedReview = reviewRepository.save(review);
                return savedReview.getReviewId();
        }

        @Override
        public boolean checkIfUserPurchasedProduct(String memberId, Long productId) {
                return orderItemRepository.existsByMemberAndProduct(memberId, productId);
        }

        private ReviewDTO entityToDto(Review review) {
                ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                reviewDTO.setCreatedAt(review.getCreatedAt().format(formatter));
                // Member 정보가 있다면 DTO에 추가하는 로직 (필요 시)
                // if (review.getMember() != null) {
                // reviewDTO.setMemberName(review.getMember().getMemberName());
                // }
                return reviewDTO;
        }
}