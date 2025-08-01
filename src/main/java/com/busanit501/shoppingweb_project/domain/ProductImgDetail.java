package com.busanit501.shoppingweb_project.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImgDetail {
    @Id
    private String uuid;
    private String fileName;
    private int ord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prduct_id")
    private Product product;

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
