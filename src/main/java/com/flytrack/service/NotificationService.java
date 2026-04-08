package com.flytrack.service;

import com.flytrack.dto.NotificationResponseDTO;
import com.flytrack.model.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(Long passengerId, Long flightId, String message, NotificationType type);
    void markAsRead(Long notificationId);
    List<NotificationResponseDTO> getPassengerNotifications(Long passengerId);
    List<NotificationResponseDTO> getUnreadPassengerNotifications(Long passengerId);
}

