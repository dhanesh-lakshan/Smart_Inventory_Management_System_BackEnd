package com.inventory.service;

import com.inventory.dto.NotificationResponse;
import com.inventory.entity.User;

import java.util.List;

public interface NotificationService {
    void notifyUser(User user, String title, String message);
    void notifyAllAdminsAndManagers(String title, String message);
    List<NotificationResponse> getMyNotifications();
    void markAsRead(Long id);
}