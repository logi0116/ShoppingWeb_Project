package com.busanit501.shoppingweb_project.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductImgDetailDTO {
    private MultipartFile file;
    private int ord;
}
