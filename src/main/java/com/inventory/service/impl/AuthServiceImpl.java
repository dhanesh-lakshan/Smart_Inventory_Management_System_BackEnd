package com.inventory.service.impl;

import com.inventory.dto.*;
import com.inventory.entity.PasswordResetToken;
import com.inventory.entity.RefreshToken;
import com.inventory.entity.User;
import com.inventory.exception.BusinessRuleViolationException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.PasswordResetTokenRepository;
import com.inventory.repository.RefreshTokenRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtUtils;
import com.inventory.security.SecurityUtils;
import com.inventory.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtUtils.generateToken(authentication);
        String refreshTokenStr = issueRefreshToken(user);

        return buildLoginResponse(user, accessToken, refreshTokenStr);
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid refresh token."));

        if (stored.getRevoked()) {
            throw new BusinessRuleViolationException("Refresh token has been revoked. Please log in again.");
        }
        if (stored.getExpiryDate().isBefore(Instant.now())) {
            throw new BusinessRuleViolationException("Refresh token has expired. Please log in again.");
        }

        User user = stored.getUser();

        // Rotate: old refresh token revoke කරලා, අලුත් එකක් issue කරනවා (security best practice)
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String newAccessToken = jwtUtils.generateTokenFromEmail(user.getEmail());
        String newRefreshToken = issueRefreshToken(user);

        return buildLoginResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found."));
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessRuleViolationException("No authenticated user found.");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect.");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email."));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(900))   // 15 minutes
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);

        // ⚠️ Production එකේදී මේ token එක email එකෙන් යවනවා, API response එකෙන් return කරන්නේ නෑ.
        // Email service integrate කරන තුරු, testing purpose එකට විතරයි return කරන්නේ.
        return token;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid or expired reset token."));

        if (resetToken.getUsed()) {
            throw new BusinessRuleViolationException("This reset token has already been used.");
        }
        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BusinessRuleViolationException("This reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // ---- helpers ----

    private String issueRefreshToken(User user) {
        String refreshTokenStr = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshTokenStr;
    }

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole().getName())
                .build();
    }
}