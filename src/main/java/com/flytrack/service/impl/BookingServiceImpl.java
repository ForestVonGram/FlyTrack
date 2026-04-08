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
import com.flytrack.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        Passenger passenger = passengerRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado: " + dto.getPassengerId()));

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado: " + dto.getFlightId()));

        if (dto.getSeatNumber() != null && !dto.getSeatNumber().isBlank()) {
            if (bookingRepository.findBySeatNumberAndFlightId(dto.getSeatNumber(), flight.getId()).isPresent()) {
                throw new BusinessException("El asiento " + dto.getSeatNumber() + " ya está ocupado en el vuelo " + flight.getFlightCode());
            }
        }

        BookingClass bookingClass;
        try {
            bookingClass = BookingClass.valueOf(dto.getBookingClass());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Clase de reserva inválida: " + dto.getBookingClass());
        }

        Booking booking = Booking.builder()
                .passenger(passenger)
                .flight(flight)
                .seatNumber(dto.getSeatNumber())
                .bookingClass(bookingClass)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMADA)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));

        booking.setStatus(BookingStatus.CANCELADA);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDTO(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        return bookingMapper.toResponseDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByPassenger(Long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new ResourceNotFoundException("Pasajero no encontrado: " + passengerId);
        }
        return bookingRepository.findByPassengerId(passengerId).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByFlight(Long flightId) {
        if (!flightRepository.existsById(flightId)) {
            throw new ResourceNotFoundException("Vuelo no encontrado: " + flightId);
        }
        return bookingRepository.findByFlightId(flightId).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}

