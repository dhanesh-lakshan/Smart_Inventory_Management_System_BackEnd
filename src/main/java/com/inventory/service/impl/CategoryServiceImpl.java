package com.inventory.service.impl;

import com.inventory.dto.CategoryRequest;
import com.inventory.dto.CategoryResponse;
import com.inventory.entity.Category;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.CategoryRepository;
import com.inventory.service.AuditLogService;
import com.inventory.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor   // constructor injection auto-generate කරනවා (Lombok)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Category saved = categoryRepository.save(category);
        auditLogService.log("CREATE_CATEGORY", "Category", saved.getId(), null, saved.getName());
        return toResponse(saved);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = findEntity(id);
        return toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntity(id);
        String oldName = category.getName(); 
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        CategoryResponse response = toResponse(categoryRepository.save(category));
        auditLogService.log("UPDATE_CATEGORY", "Category", id, oldName, request.getName());   
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findEntity(id);
        categoryRepository.delete(category);
        auditLogService.log("DELETE_CATEGORY", "Category", id, category.getName(), null);
    }

    private Category findEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    private CategoryResponse toResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .createdAt(c.getCreatedAt())
                .build();
    }
}