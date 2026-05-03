package com.flytrack.service.impl;

import com.flytrack.dto.NotificationResponseDTO;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.NotificationMapper;
import com.flytrack.model.Flight;
import com.flytrack.model.Notification;
import com.flytrack.model.Passenger;
import com.flytrack.model.User;
import com.flytrack.model.enums.NotificationType;
import com.flytrack.repository.FlightRepository;
import com.flytrack.repository.NotificationRepository;
import com.flytrack.repository.PassengerRepository;
import com.flytrack.repository.UserRepository;
import com.flytrack.service.MailService;
import com.flytrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void createNotification(Long passengerId, Long flightId, String message, NotificationType type) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado"));

        Flight flight = flightId != null ? flightRepository.findById(flightId).orElse(null) : null;

        Notification notification = Notification.builder()
                .passenger(passenger)
                .flight(flight)
                .message(message)
                .type(type)
                .notificationTime(LocalDateTime.now())
                .read(false)
                .build();

        notificationRepository.save(notification);

        // Notify by email
        String subject = "FlyTrack Notification: " + type.name();
        mailService.sendSimpleEmail(passenger.getEmail(), subject, message);
    }

    @Override
    @Transactional
    public void createUserNotification(Long userId, String message, NotificationType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .notificationTime(LocalDateTime.now())
                .read(false)
                .build();

        notificationRepository.save(notification);

        // Notify by email
        String subject = "FlyTrack Notification: " + type.name();
        mailService.sendSimpleEmail(user.getEmail(), subject, message);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + notificationId));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getPassengerNotifications(Long passengerId) {
        List<Notification> notifications = notificationRepository.findByPassengerIdOrderByNotificationTimeDesc(passengerId);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadPassengerNotifications(Long passengerId) {
        List<Notification> notifications = notificationRepository.findByPassengerIdAndReadFalse(passengerId);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAuthenticatedUserNotifications(String email) {
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado con email: " + email));
        return getPassengerNotifications(passenger.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByNotificationTimeDesc(userId);
        return notificationMapper.toResponseDTOList(notifications);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByNotificationTimeDesc(userId);
        return notificationMapper.toResponseDTOList(notifications);
    }
}
