package com.flytrack.repository;

import com.flytrack.model.Flight;
import com.flytrack.model.enums.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByStatus(FlightStatus status);

    List<Flight> findByOriginAndDestination(String origin, String destination);

    List<Flight> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Flight> findByFlightCode(String flightCode);

    List<Flight> findByAirline(String airline);
}

