package com.inventory.service.impl;

import com.inventory.dto.ProductRequest;
import com.inventory.dto.ProductResponse;
import com.inventory.entity.Category;
import com.inventory.entity.Product;
import com.inventory.entity.Supplier;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.AuditLogService;
import com.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateResourceException("Product SKU already exists: " + request.getSku());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + request.getSupplierId()));

        Product product = Product.builder()
                .sku(request.getSku())
                .productName(request.getProductName())
                .description(request.getDescription())
                .category(category)
                .supplier(supplier)
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .stockQuantity(0)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 0)
                .active(true)
                .build();
        Product saved = productRepository.save(product);
        auditLogService.log("CREATE_PRODUCT", "Product", saved.getId(), null, saved.getSku());
        return toResponse(saved);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ProductResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntity(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + request.getSupplierId()));

        product.setSku(request.getSku());
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setCostPrice(request.getCostPrice());
        product.setSellingPrice(request.getSellingPrice());
        if (request.getMinimumStock() != null) {
            product.setMinimumStock(request.getMinimumStock());
        }
        Product saved = productRepository.save(product);
        auditLogService.log("UPDATE_PRODUCT", "Product", id, null, request.getSku());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findEntity(id);
        productRepository.delete(product);
        auditLogService.log("DELETE_PRODUCT", "Product", id, product.getProductName(), null);   // ← මේ line එක add කරන්න
    }

    @Override
    @Transactional
    public void updateImageUrl(Long id, String imageUrl) {
        Product product = findEntity(id);
        product.setImageUrl(imageUrl);
        productRepository.save(product);
    }

    private Product findEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .productName(p.getProductName())
                .description(p.getDescription())
                .category(p.getCategory().getName())
                .supplier(p.getSupplier().getCompanyName())
                .costPrice(p.getCostPrice())
                .sellingPrice(p.getSellingPrice())
                .stockQuantity(p.getStockQuantity())
                .minimumStock(p.getMinimumStock())
                .imageUrl(p.getImageUrl())
                .active(p.getActive())
                .createdAt(p.getCreatedAt())
                .build();
    }

    @Override
    public Page<ProductResponse> search(String keyword, Long categoryId, Pageable pageable) {
        return productRepository.search(keyword, categoryId, pageable).map(this::toResponse);
    }
}