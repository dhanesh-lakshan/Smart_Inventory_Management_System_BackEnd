package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.ProfileResponse;
import com.inventory.dto.UpdateProfileRequest;
import com.inventory.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved.", profileService.getMyProfile()));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully.", profileService.updateMyProfile(request)));
    }
}