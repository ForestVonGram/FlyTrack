package com.flytrack.service;

import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO dto);
    BookingResponseDTO cancelBooking(Long id);
    BookingResponseDTO getBookingById(Long id);
    List<BookingResponseDTO> getBookingsByPassenger(Long passengerId);
    List<BookingResponseDTO> getBookingsByFlight(Long flightId);
}

