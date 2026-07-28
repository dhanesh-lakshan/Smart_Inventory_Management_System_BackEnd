package com.inventory.service.impl;

import com.inventory.dto.NotificationResponse;
import com.inventory.entity.Notification;
import com.inventory.entity.User;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.NotificationRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.SecurityUtils;
import com.inventory.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void notifyUser(User user, String title, String message) {
        Notification n = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(n);
    }

    @Override
    @Transactional
    public void notifyAllAdminsAndManagers(String title, String message) {
        List<User> recipients = userRepository.findAll().stream()
                .filter(u -> u.getRole() != null &&
                        (u.getRole().getName().equals("ADMIN") || u.getRole().getName().equals("MANAGER")))
                .toList();
        recipients.forEach(u -> notifyUser(u, title, message));
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .isRead(n.getIsRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        n.setIsRead(true);
        notificationRepository.save(n);
    }
}