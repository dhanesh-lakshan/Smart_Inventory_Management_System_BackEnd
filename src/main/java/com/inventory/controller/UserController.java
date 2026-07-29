package com.inventory.controller;

import com.inventory.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.inventory.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // ← class-level: මුළු controller එකම ADMIN විතරයි
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully.", userService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved.", userService.search(keyword, role, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User retrieved.", userService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User updated successfully.", userService.update(id, request)));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<Void>> disable(@PathVariable Long id) {
        userService.disable(id);
        return ResponseEntity.ok(ApiResponse.success("User disabled successfully.", null));
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<Void>> enable(@PathVariable Long id) {
        userService.enable(id);
        return ResponseEntity.ok(ApiResponse.success("User enabled successfully.", null));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> assignRole(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully.", userService.assignRole(id, request)));
    }
}