package com.inventory.service.impl;

import com.inventory.dto.*;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import com.inventory.service.AuditLogService;
import com.inventory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("A user with this email already exists: " + request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleId()));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .active(true)
                .build();

        User saved = userRepository.save(user);
        auditLogService.log("CREATE_USER", "User", saved.getId(), null, saved.getEmail());

        return toResponse(saved);
    }

    @Override
    public Page<UserResponse> search(String keyword, String roleName, Pageable pageable) {
        return userRepository.search(keyword, roleName, pageable).map(this::toResponse);
    }

    @Override
    public UserResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = findEntity(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void disable(Long id) {
        User user = findEntity(id);
        user.setActive(false);
        userRepository.save(user);
        auditLogService.log("DISABLE_USER", "User", id, null, null);
    }

    @Override
    @Transactional
    public void enable(Long id) {
        User user = findEntity(id);
        user.setActive(true);
        userRepository.save(user);
        auditLogService.log("ENABLE_USER", "User", id, null, null);
    }

    @Override
    @Transactional
    public UserResponse assignRole(Long id, AssignRoleRequest request) {
        User user = findEntity(id);
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleId()));

        String oldRole = user.getRole().getName();
        user.setRole(role);
        User saved = userRepository.save(user);
        auditLogService.log("ASSIGN_ROLE", "User", id, oldRole, role.getName());

        return toResponse(saved);
    }

    private User findEntity(Long id) {
        return userRepository.findByIdWithRole(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .active(u.getActive())
                .role(u.getRole().getName())
                .createdAt(u.getCreatedAt())
                .build();
    }
}