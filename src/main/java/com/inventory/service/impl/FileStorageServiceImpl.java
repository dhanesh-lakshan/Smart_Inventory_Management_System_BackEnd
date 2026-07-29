package com.inventory.service.impl;

import com.inventory.exception.BusinessRuleViolationException;
import com.inventory.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public String storeProductImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BusinessRuleViolationException("Uploaded file is empty.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessRuleViolationException("Only JPG, PNG, and WEBP images are allowed.");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessRuleViolationException("File size must not exceed 5MB.");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String uniqueFilename = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // API එකෙන් access කරන්න පුළුවන් relative path එකක් return කරනවා
            return "/uploads/products/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}