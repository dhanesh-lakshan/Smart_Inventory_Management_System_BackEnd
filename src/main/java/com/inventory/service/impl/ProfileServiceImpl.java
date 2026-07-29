package com.inventory.service.impl;

import com.inventory.dto.ProfileResponse;
import com.inventory.dto.UpdateProfileRequest;
import com.inventory.entity.User;
import com.inventory.exception.BusinessRuleViolationException;
import com.inventory.repository.UserRepository;
import com.inventory.security.SecurityUtils;
import com.inventory.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    public ProfileResponse getMyProfile() {
        return toResponse(currentUserOrThrow());
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = currentUserOrThrow();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        return toResponse(userRepository.save(user));
    }

    private User currentUserOrThrow() {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new BusinessRuleViolationException("No authenticated user found.");
        }
        return user;
    }

    private ProfileResponse toResponse(User u) {
        return ProfileResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole().getName())
                .build();
    }
}