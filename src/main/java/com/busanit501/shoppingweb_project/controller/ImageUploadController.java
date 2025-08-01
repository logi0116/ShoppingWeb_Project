package com.busanit501.shoppingweb_project.controller;

import com.busanit501.shoppingweb_project.domain.Product;
import com.busanit501.shoppingweb_project.domain.ProductImage;
import com.busanit501.shoppingweb_project.repository.ProductImageRepository;
import com.busanit501.shoppingweb_project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Log4j2
public class ImageUploadController {

    @Value("c:\\upload\\WebShopingDetailImg")
    private String uploadPath;

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
            @RequestParam("productId") Long productId) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("파일을 선택해 주세요.", HttpStatus.BAD_REQUEST);
        }
        Product product = productRepository.findByProductId(productId);
        try {
            String originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalFileName;

            File destinationFile = new File(uploadPath, savedFileName);

            System.out.println("### 파일 저장될 최종 경로: " + destinationFile.getAbsolutePath());

            file.transferTo(destinationFile);

            ProductImage productImage = ProductImage.builder()
                    .fileName(savedFileName)
                    .ord(0)
                    .thumbnail(true)
                    .build();
            product.addImage(productImage);
            productImageRepository.save(productImage);

            return new ResponseEntity<>("파일 업로드 성공 및 DB 저장 완료: " + savedFileName, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("파일 업로드 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("알 수 없는 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/display/{fileName}")
    public ResponseEntity<Resource> displayImage(@PathVariable String fileName) {
        try {
            log.info("지금 잘 되고 있므");
            // 경로 우회 방지 (예: ../../etc/passwd 방지)
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                return ResponseEntity.badRequest().build();
            }

            Path filePath = Paths.get(uploadPath, fileName).normalize();
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // MIME 타입 추론
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}