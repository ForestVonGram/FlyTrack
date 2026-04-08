package com.flytrack.service.impl;

import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.BookingMapper;
import com.flytrack.model.Booking;
import com.flytrack.model.Flight;
import com.flytrack.model.Passenger;
import com.flytrack.model.enums.BookingClass;
import com.flytrack.model.enums.BookingStatus;
import com.flytrack.repository.BookingRepository;
import com.flytrack.repository.FlightRepository;
import com.flytrack.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequestDTO requestDTO;
    private Passenger passenger;
    private Flight flight;
    private Booking booking;

    @BeforeEach
    void setUp() {
        requestDTO = new BookingRequestDTO();
        requestDTO.setPassengerId(1L);
        requestDTO.setFlightId(2L);
        requestDTO.setSeatNumber("10A");
        requestDTO.setBookingClass("ECONOMICA");

        passenger = new Passenger();
        passenger.setId(1L);

        flight = new Flight();
        flight.setId(2L);
        flight.setFlightCode("FL123");

        booking = new Booking();
        booking.setId(100L);
        booking.setPassenger(passenger);
        booking.setFlight(flight);
        booking.setStatus(BookingStatus.CONFIRMADA);
        booking.setSeatNumber("10A");
        booking.setBookingClass(BookingClass.ECONOMICA);
    }

    @Test
    void createBooking_Success() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(flightRepository.findById(2L)).thenReturn(Optional.of(flight));
        when(bookingRepository.findBySeatNumberAndFlightId("10A", 2L)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setId(100L);
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.createBooking(requestDTO);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_PassengerNotFound_ThrowsException() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                bookingService.createBooking(requestDTO)
        );
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_SeatOccupied_ThrowsBusinessException() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(flightRepository.findById(2L)).thenReturn(Optional.of(flight));

        Booking occupiedBooking = new Booking();
        when(bookingRepository.findBySeatNumberAndFlightId("10A", 2L)).thenReturn(Optional.of(occupiedBooking));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                bookingService.createBooking(requestDTO)
        );

        assertTrue(ex.getMessage().contains("ya está ocupado"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBooking_Success() {
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setStatus("CANCELADA");
        when(bookingMapper.toResponseDTO(booking)).thenReturn(responseDTO);

        BookingResponseDTO result = bookingService.cancelBooking(100L);

        assertNotNull(result);
        assertEquals("CANCELADA", result.getStatus());
        assertEquals(BookingStatus.CANCELADA, booking.getStatus());
    }
}

