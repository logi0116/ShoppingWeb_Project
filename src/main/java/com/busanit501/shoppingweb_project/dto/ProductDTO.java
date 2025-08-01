package com.busanit501.shoppingweb_project.dto;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.enums.ProductCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long productId;

    @NotEmpty
    private String productName;

    @NotNull
    private BigDecimal price;

    private int stock;

    private ProductCategory productTag;
    private String thumbnailFileName;
    private List<String> fileNames;

    //리뷰 개수와 평균 평점
    private long reviewCount;
    private double averageRating;


    public void setThumbnailFileName(String fileName) {
        this.thumbnailFileName = fileName;
    }
}