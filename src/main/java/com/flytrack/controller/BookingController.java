package com.flytrack.controller;

import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.dto.BookingPassengerRequestDTO;
import com.flytrack.dto.FlightSummaryDTO;
import com.flytrack.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBooking(@Valid @RequestBody BookingRequestDTO dto) {
        return bookingService.createBooking(dto);
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBookingForPassenger(@Valid @RequestBody BookingPassengerRequestDTO dto) {
        return bookingService.createBookingForCurrentPassenger(dto);
    }

    @DeleteMapping("/me/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelMyBooking(@PathVariable Long id) {
        bookingService.cancelBookingForCurrentPassenger(id);
    }

    @DeleteMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping("/passenger/{passengerId}")
    public List<BookingResponseDTO> getBookingsByPassenger(@PathVariable Long passengerId) {
        return bookingService.getBookingsByPassenger(passengerId);
    }

    @GetMapping("/me")
    public List<BookingResponseDTO> getMyBookings() {
        return bookingService.getBookingsForCurrentPassenger();
    }

    @GetMapping("/me/flights")
    public List<FlightSummaryDTO> getMyFlights() {
        return bookingService.getFlightsForCurrentPassenger();
    }

    @GetMapping("/flight/{flightId}")
    public List<BookingResponseDTO> getBookingsByFlight(@PathVariable Long flightId) {
        return bookingService.getBookingsByFlight(flightId);
    }
}

