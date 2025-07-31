package com.busanit501.shoppingweb_project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileUploadService {

    private final String uploadDir = "C:/upload_images";

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String savedFileName = System.currentTimeMillis() + "_" + originalFilename;

        Path savePath = Paths.get(uploadDir, savedFileName);
        Files.createDirectories(savePath.getParent());
        file.transferTo(savePath.toFile());

        // 클라이언트가 접근할 수 있는 URL 경로 반환
        return "/images/" + savedFileName;
    }
}
