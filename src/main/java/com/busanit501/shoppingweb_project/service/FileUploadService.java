package com.busanit501.shoppingweb_project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service

public class FileUploadService {

    @Value("${com.busanit501.upload.path}")
    private String UPLOAD_DIR;

    public String saveFile(MultipartFile file) throws IOException {
        try {
            String uuid = UUID.randomUUID().toString();
            String originalName = file.getOriginalFilename();

            String savedName = uuid + "-" + originalName;

            File uploadPath = new File(UPLOAD_DIR);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File dest = new File(uploadPath, savedName);
            file.transferTo(dest);

            return savedName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }

    private String extractUUID(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
