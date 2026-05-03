package com.flytrack.repository;

import com.flytrack.model.Booking;
import com.flytrack.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByUserId(Long userId);
    List<Booking> findByFlightId(Long flightId);

    // Optional<Booking> findBySeatNumberAndFlightId(String seatNumber, Long flightId);

    @Query("SELECT b FROM Booking b JOIN b.passengers p WHERE p.id = :passengerId AND b.status IN :activeStatuses")
    List<Booking> findActiveBookingsByPassengerId(@Param("passengerId") Long passengerId, @Param("activeStatuses") List<BookingStatus> activeStatuses);
}

