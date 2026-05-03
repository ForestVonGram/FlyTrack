package com.flytrack.controller;

import com.flytrack.dto.NotificationResponseDTO;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.model.Booking;
import com.flytrack.model.User;
import com.flytrack.repository.BookingRepository;
import com.flytrack.repository.UserRepository;
import com.flytrack.service.CurrentUserService;
import com.flytrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/me")
    public List<NotificationResponseDTO> getMyNotifications() {
        String username = currentUserService.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Only return unread notifications as per requirements
        return notificationService.getUnreadUserNotifications(user.getId());
    }

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
