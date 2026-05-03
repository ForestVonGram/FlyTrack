package com.flytrack.service;

import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingPassengerRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.dto.FlightSummaryDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO dto);
    BookingResponseDTO createBookingForCurrentPassenger(BookingPassengerRequestDTO dto);
    BookingResponseDTO cancelBooking(Long id);
    void cancelBookingForCurrentPassenger(Long id);
    BookingResponseDTO getBookingById(Long id);
    List<BookingResponseDTO> getBookingsByPassenger(Long passengerId);
    List<BookingResponseDTO> getBookingsForCurrentPassenger();
    List<FlightSummaryDTO> getFlightsForCurrentPassenger();
    List<BookingResponseDTO> getBookingsByFlight(Long flightId);
}

