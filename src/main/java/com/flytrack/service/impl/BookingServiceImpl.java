package com.flytrack.service.impl;
import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BookingPassengerRequestDTO;
import com.flytrack.dto.BookingRequestDTO;
import com.flytrack.dto.BookingResponseDTO;
import com.flytrack.dto.FlightSummaryDTO;
import com.flytrack.dto.PassengerBookingRequestDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.BookingMapper;
import com.flytrack.mapper.FlightMapper;
import com.flytrack.model.Baggage;
import com.flytrack.model.Booking;
import com.flytrack.model.Flight;
import com.flytrack.model.Passenger;
import com.flytrack.model.User;
import com.flytrack.model.enums.BaggageStatus;
import com.flytrack.model.enums.BookingClass;
import com.flytrack.model.enums.BookingStatus;
import com.flytrack.repository.BookingRepository;
import com.flytrack.repository.FlightRepository;
import com.flytrack.repository.PassengerRepository;
import com.flytrack.repository.UserRepository;
import com.flytrack.service.BookingService;
import com.flytrack.service.CurrentUserService;
import com.flytrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final FlightMapper flightMapper;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        } else {
            String username = currentUserService.getUsername();
            if (username != null) {
                user = userRepository.findByUsername(username).orElse(null);
            }
        }
        return createBookingInternal(user, dto.getFlightId(), dto.getBookingClass(), dto.getPassengers());
    }
    @Override
    @Transactional
    public BookingResponseDTO createBookingForCurrentPassenger(BookingPassengerRequestDTO dto) {
        String username = currentUserService.getUsername();
        if (username == null) {
             throw new ResourceNotFoundException("Sesión no válida");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return createBookingInternal(user, dto.getFlightId(), dto.getBookingClass(), dto.getPassengers());
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
    @Transactional
    public void cancelBookingForCurrentPassenger(Long id) {
        String username = currentUserService.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + id));
        if (booking.getUser() == null || !booking.getUser().getId().equals(user.getId())) {
             throw new BusinessException("No tienes permiso para cancelar esta reserva");
        }
        booking.setStatus(BookingStatus.CANCELADA);
        bookingRepository.save(booking);
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
        // En el nuevo modelo, buscamos reservas que tengan a este pasajero
        Passenger p = passengerRepository.findById(passengerId).orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado"));
        List<Booking> bookings = List.of(p.getBooking());
        return bookings.stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsForCurrentPassenger() {
        String username = currentUserService.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        // Encontramos reservas asociadas a este usuario
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(bookingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<FlightSummaryDTO> getFlightsForCurrentPassenger() {
        String username = currentUserService.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(Booking::getFlight)
                .map(flightMapper::toSummaryDTO)
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
    private BookingResponseDTO createBookingInternal(User user, Long flightId, String bookingClassValue, List<PassengerBookingRequestDTO> passengersData) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado: " + flightId));
        BookingClass bookingClass;
        try {
            // Map english values requested by standard to internal Spanish enum to satisfy DB constraints
            String normalizedClass = bookingClassValue.toUpperCase();
            switch (normalizedClass) {
                case "ECONOMY": normalizedClass = "ECONOMICA"; break;
                case "BUSINESS": normalizedClass = "EJECUTIVA"; break;
                case "FIRST": normalizedClass = "PRIMERA"; break;
            }
            bookingClass = BookingClass.valueOf(normalizedClass);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Clase de reserva inválida: " + bookingClassValue);
        }
        // Crear reserva
        Booking booking = Booking.builder()
                .user(user)
                .flight(flight)
                .bookingClass(bookingClass)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMADA)
                .passengers(new ArrayList<>())
                .build();
        // Procesar pasajeros y equipajes
        for (PassengerBookingRequestDTO pDto : passengersData) {
            // Validar asiento si aplica 
            // NOTA: Para validar asientos globalmente habría que checar en listado de pasajeros del vuelo actual (simplificado aqui)
            Passenger passenger = Passenger.builder()
                    .firstName(pDto.getFirstName())
                    .lastName(pDto.getLastName())
                    .email(pDto.getEmail())
                    .identityDocument(pDto.getIdentityDocument())
                    .phoneNumber(pDto.getPhoneNumber())
                    .seatNumber(pDto.getSeatNumber())
                    .booking(booking)
                    .baggages(new ArrayList<>())
                    .build();
            if (pDto.getBaggages() != null) {
                for (BaggageRequestDTO bDto : pDto.getBaggages()) {
                    Baggage baggage = Baggage.builder()
                            .passenger(passenger)
                            .trackingCode(UUID.randomUUID().toString().substring(0, 10).toUpperCase()) // Tracking unico
                            .weight(bDto.getWeight())
                            .status(BaggageStatus.REGISTRADO)
                            .build();
                    passenger.getBaggages().add(baggage);
                }
            }
            booking.getPassengers().add(passenger);
        }

        Booking savedBooking = bookingRepository.save(booking);

        // Notify User
        if (user != null) {
            notificationService.createUserNotification(
                    user.getId(),
                    "Tu reserva para el vuelo " + flight.getFlightNumber() + " ha sido confirmada.",
                    NotificationType.RESERVA_CREADA
            );
        }

        // Notify Passengers
        for (Passenger passenger : savedBooking.getPassengers()) {
            notificationService.createNotification(
                    passenger.getId(),
                    flight.getId(),
                    "Tu asiento " + passenger.getSeatNumber() + " para el vuelo " + flight.getFlightNumber() + " ha sido reservado.",
                    NotificationType.RESERVA_CREADA
            );
        }

        return bookingMapper.toResponseDTO(savedBooking);
    }
}
