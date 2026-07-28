package com.inventory.service;

import com.inventory.dto.LoginRequest;
import com.inventory.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}