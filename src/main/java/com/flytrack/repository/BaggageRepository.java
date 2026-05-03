package com.flytrack.repository;

import com.flytrack.model.Baggage;
import com.flytrack.model.enums.BaggageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaggageRepository extends JpaRepository<Baggage, Long> {

    Optional<Baggage> findByTrackingCode(String trackingCode);

    List<Baggage> findByPassengerId(Long bookingId);

    List<Baggage> findByStatus(BaggageStatus status);
}

