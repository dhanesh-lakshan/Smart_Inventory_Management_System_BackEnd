package com.inventory.service;

import com.inventory.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    Page<UserResponse> search(String keyword, String roleName, Pageable pageable);
    UserResponse getById(Long id);
    UserResponse update(Long id, UserUpdateRequest request);
    void disable(Long id);
    void enable(Long id);
    UserResponse assignRole(Long id, AssignRoleRequest request);
}