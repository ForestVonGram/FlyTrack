package com.flytrack.controller;

import com.flytrack.dto.NotificationResponseDTO;
import com.flytrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/passenger/{passengerId}")
    public List<NotificationResponseDTO> getAllNotifications(@PathVariable Long passengerId) {
        return notificationService.getPassengerNotifications(passengerId);
    }

    @GetMapping("/passenger/{passengerId}/unread")
    public List<NotificationResponseDTO> getUnreadNotifications(@PathVariable Long passengerId) {
        return notificationService.getUnreadPassengerNotifications(passengerId);
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}

