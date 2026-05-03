package com.flytrack.repository;

import com.flytrack.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByPassengerIdOrderByNotificationTimeDesc(Long passengerId);

    List<Notification> findByPassengerIdAndReadFalse(Long passengerId);

    long countByPassengerIdAndReadFalse(Long passengerId);

    List<Notification> findByUserIdOrderByNotificationTimeDesc(Long userId);

    List<Notification> findByUserIdAndReadFalseOrderByNotificationTimeDesc(Long userId);
}
