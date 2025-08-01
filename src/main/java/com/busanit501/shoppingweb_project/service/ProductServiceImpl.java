package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.ProductImage;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import com.busanit501.shoppingweb_project.dto.PageRequestDTO;
import com.busanit501.shoppingweb_project.dto.PageResponseDTO;
import com.busanit501.shoppingweb_project.dto.ProductDTO;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import com.busanit501.shoppingweb_project.repository.ReviewRepository;
import com.busanit501.shoppingweb_project.repository.ProductImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional()
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository; // 리뷰 정보 계산을 위해 주입
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;
    private final FileUploadService fileUploadService;

    @Override
    public ProductDTO getProductById(Long productId) {
        log.info("ProductService - getProductById: " + productId);

        // 1. 상품을 ID로 조회하고, 없으면 예외를 발생시킵니다. (중복된 orElseThrow 해결)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. id=" + productId));

        // 2. Product Entity를 ProductDTO로 변환합니다. (팀 기능: 이미지 정보 포함)
        // Product.entityToDTO는 썸네일과 상세 이미지 파일명을 모두 설정해줍니다.
        ProductDTO productDTO = Product.entityToDTO(product);
        log.info("ProductService - 이미지 정보 포함 DTO: " + productDTO);

        // 3. 리뷰 개수와 평균 평점을 계산하여 DTO에 추가합니다. (내 기능)
        long reviewCount = reviewRepository.countByProduct(product);
        Double avgRating = reviewRepository.findAverageRatingByProduct(product);

        productDTO.setReviewCount(reviewCount);
        productDTO.setAverageRating(avgRating != null ? avgRating : 0.0);
        log.info("ProductService - 리뷰 정보 추가 후 DTO: " + productDTO);

        // 4. 모든 정보가 포함된 DTO를 반환합니다.
        return productDTO;
    }

    @Override
    public PageResponseDTO<ProductDTO> getProductList(PageRequestDTO pageRequestDTO) {
        // 1. 페이징 쿼리로 상품 목록을 가져옵니다. (내 기능)
        Page<Product> result = productRepository.search(pageRequestDTO);

        // 2. 각 상품을 DTO로 변환합니다.
        List<ProductDTO> dtoList = result.getContent().stream()
                .map(product -> {
                    // 2-1. 먼저 팀의 메서드를 호출해 이미지 정보가 담긴 DTO를 받습니다. (팀 코드 재사용)
                    ProductDTO dto = this.mapProductToDtoWithImage(product);

                    // 2-2. 그 DTO에 리뷰 정보를 추가합니다. (내 기능 추가)
                    long reviewCount = reviewRepository.countByProduct(product);
                    Double avgRating = reviewRepository.findAverageRatingByProduct(product);
                    dto.setReviewCount(reviewCount);
                    dto.setAverageRating(avgRating != null ? avgRating : 0.0);

                    return dto;
                })
                .collect(Collectors.toList());

        // 3. 최종 페이징 결과를 반환합니다.
        return PageResponseDTO.<ProductDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .totalCount(result.getTotalElements())
                .build();
    }

    // 새 상품 등록 메서드 구현
    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        if (productDTO.getProductTag() == null) {
            productDTO.setProductTag(ProductCategory.UNKNOWN);
        }
        Product product = productRepository.findByProductId(productDTO.getProductId());
        Product savedProduct = productRepository.save(product);
        return Product.entityToDTO(savedProduct);
    }

    // 상품 수정 메서드 구현
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        log.info("ProductService에서 작업중 수정된 ProductDTO : " + productDTO.getProductName());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. id=" + productId));
        // 상품 정보 수정
        product.changeTitleContent(productDTO);
        log.info("ProductService에서 작업중 수정된 Product : " + product.getProductName());
        Product updatedProduct = productRepository.save(product);
        return Product.entityToDTO(updatedProduct);
    }

    // 상품 삭제 메서드 구현
    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public void createProductWithImages(String productName, BigDecimal price, int stock, ProductCategory productTag,
            MultipartFile thumbnail, List<MultipartFile> detailImages) {
        Product product = Product.builder()
                .productName(productName)
                .price(price)
                .stock(stock)
                .productTag(productTag)
                .build();
        log.info("ProductService에서 작업중 관리자가 생성한 Product : " + product.getProductName());
        productRepository.save(product);

        // 2. 섬네일 이미지 저장 thumnail = true로 저장
        if (thumbnail != null && !thumbnail.isEmpty()) {
            try {
                String savedFileName = fileUploadService.saveFile(thumbnail);
                ProductImage thumbnailEntity = ProductImage.builder()
                        .fileName(savedFileName)
                        .ord(0)
                        .thumbnail(true)
                        .build();
                product.addImage(thumbnailEntity);
                log.info("ProductService에서 작업중 썸네일 이미지인지 확인 : " + thumbnailEntity.getFileName());
                productImageRepository.save(thumbnailEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 상세 이미지 DB에 저장 thumnail = false로 표시해주기
            if (detailImages != null && !detailImages.isEmpty()) {
                int order = 1;
                for (MultipartFile file : detailImages) {
                    try {
                        if (!file.isEmpty()) {
                            String savedFileName = fileUploadService.saveFile(file);
                            ProductImage detailImageEntity = ProductImage.builder()
                                    .fileName(savedFileName)
                                    .ord(order++)
                                    .thumbnail(false)
                                    .build();
                            product.addImage(detailImageEntity);
                            log.info("ProductService에서 작업중 썸네일 이미지인지 확인 : " + detailImageEntity.getFileName());
                            productImageRepository.save(detailImageEntity);
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    // Helper method to map Product to ProductDTO and attach image filename
    private ProductDTO mapProductToDtoWithImage(Product product) {
        ProductDTO dto = Product.entityToDTO(product);
        productImageRepository.findByProduct_ProductIdAndThumbnail(product.getProductId(), true)
                .ifPresent(productImage -> dto.setThumbnailFileName(productImage.getFileName()));
        return dto;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        log.info("ProductService - getAllProducts 호출");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> {
                    // 팀의 이미지 DTO 변환 메서드를 먼저 호출하고
                    ProductDTO dto = this.mapProductToDtoWithImage(product);
                    // 나의 리뷰 정보 계산 로직을 추가합니다.
                    long reviewCount = reviewRepository.countByProduct(product);
                    Double avgRating = reviewRepository.findAverageRatingByProduct(product);
                    dto.setReviewCount(reviewCount);
                    dto.setAverageRating(avgRating != null ? avgRating : 0.0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        log.info("ProductService - getProductsByCategory: " + category);
        ProductCategory productCategory = ProductCategory.fromKoreanName(category);
        List<Product> products = productRepository.findByProductTag(productCategory);
        return products.stream()
                .map(product -> {
                    ProductDTO dto = this.mapProductToDtoWithImage(product);
                    long reviewCount = reviewRepository.countByProduct(product);
                    Double avgRating = reviewRepository.findAverageRatingByProduct(product);
                    dto.setReviewCount(reviewCount);
                    dto.setAverageRating(avgRating != null ? avgRating : 0.0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        log.info("ProductService - searchProducts: " + keyword);
        List<Product> products = productRepository.searchByKeyword(keyword);
        return products.stream()
                .map(product -> {
                    ProductDTO dto = this.mapProductToDtoWithImage(product);
                    long reviewCount = reviewRepository.countByProduct(product);
                    Double avgRating = reviewRepository.findAverageRatingByProduct(product);
                    dto.setReviewCount(reviewCount);
                    dto.setAverageRating(avgRating != null ? avgRating : 0.0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }
}
