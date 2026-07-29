package com.inventory.service;

import com.inventory.dto.ProfileResponse;
import com.inventory.dto.UpdateProfileRequest;

public interface ProfileService {
    ProfileResponse getMyProfile();
    ProfileResponse updateMyProfile(UpdateProfileRequest request);
}