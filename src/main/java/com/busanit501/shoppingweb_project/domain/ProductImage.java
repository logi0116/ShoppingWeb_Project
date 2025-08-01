package com.busanit501.shoppingweb_project.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_image")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@ToString
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "file_name", length = 500, nullable = false)
    private String fileName;

    @Column(name = "ord", nullable = false)
    @Builder.Default
    private int ord = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @Column(name = "is_thumbnail", nullable = false)
    @Builder.Default
    private boolean thumbnail = false;

    public void setProduct(Product product) {
        this.product = product;
        if (!product.getImageSet().contains(this)) {
            product.getImageSet().add(this);
        }
    }
    public void changeFileName(String fileName) {
        this.fileName = fileName;
    }

    public void changeOrd(int ord) {
        this.ord = ord;
    }

    public void changeThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

}