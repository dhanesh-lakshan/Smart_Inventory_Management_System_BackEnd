package com.inventory.service;

public interface FileStorageService {
    String storeProductImage(org.springframework.web.multipart.MultipartFile file);
}