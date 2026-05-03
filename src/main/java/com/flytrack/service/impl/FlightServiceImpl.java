package com.flytrack.service.impl;

import com.flytrack.dto.FlightRequestDTO;
import com.flytrack.dto.FlightResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.FlightMapper;
import com.flytrack.model.Booking;
import com.flytrack.model.Flight;
import com.flytrack.model.enums.FlightStatus;
import com.flytrack.model.enums.NotificationType;
import com.flytrack.repository.BookingRepository;
import com.flytrack.repository.FlightRepository;
import com.flytrack.service.FlightService;
import com.flytrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final FlightMapper flightMapper;

    @Override
    @Transactional
    public FlightResponseDTO createFlight(FlightRequestDTO dto) {
        if (flightRepository.findByFlightCode(dto.getFlightCode()).isPresent()) {
            throw new BusinessException("El código de vuelo ya existe: " + dto.getFlightCode());
        }

        Flight flight = flightMapper.toEntity(dto);
        Flight savedFlight = flightRepository.save(flight);
        return flightMapper.toResponseDTO(savedFlight);
    }

    @Override
    @Transactional
    public FlightResponseDTO updateFlight(Long id, FlightRequestDTO dto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));

        if (!flight.getFlightCode().equals(dto.getFlightCode()) && flightRepository.findByFlightCode(dto.getFlightCode()).isPresent()) {
            throw new BusinessException("El código de vuelo ya existe: " + dto.getFlightCode());
        }

        flight.setFlightCode(dto.getFlightCode());
        flight.setOrigin(dto.getOrigin());
        flight.setDestination(dto.getDestination());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setEstimatedArrivalTime(dto.getEstimatedArrivalTime());

        try {
            flight.setStatus(FlightStatus.valueOf(dto.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de vuelo inválido: " + dto.getStatus());
        }

        flight.setGate(dto.getGate());
        flight.setAirline(dto.getAirline());

        Flight updatedFlight = flightRepository.save(flight);
        return flightMapper.toResponseDTO(updatedFlight);
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vuelo no encontrado con id: " + id);
        }
        flightRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));
        return flightMapper.toResponseDTO(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getAllFlights() {
        return flightMapper.toResponseDTOList(flightRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getFlightsByStatus(String status) {
        try {
            FlightStatus flightStatus = FlightStatus.valueOf(status);
            return flightMapper.toResponseDTOList(flightRepository.findByStatus(flightStatus));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de vuelo inválido: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightResponseDTO> getFlightsByOriginAndDestination(String origin, String destination) {
        return flightMapper.toResponseDTOList(flightRepository.findByOriginAndDestination(origin, destination));
    }

    @Override
    @Transactional
    public FlightResponseDTO changeStatus(Long id, String status) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));

        FlightStatus newStatus;
        try {
            newStatus = FlightStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de vuelo inválido: " + status);
        }

        flight.setStatus(newStatus);
        Flight updatedFlight = flightRepository.save(flight);

        if (newStatus == FlightStatus.RETRASADO || newStatus == FlightStatus.CANCELADO) {
            List<Booking> bookings = bookingRepository.findByFlightId(id);
            NotificationType type = (newStatus == FlightStatus.RETRASADO) ? NotificationType.RETRASO : NotificationType.CANCELACION;
            String message = "El vuelo " + flight.getFlightCode() + " ha cambiado su estado a " + status;

            for (Booking booking : bookings) {
                if (booking.getUser() != null) {
                    notificationService.createUserNotification(booking.getUser().getId(), message, type);
                }
                for (com.flytrack.model.Passenger passenger : booking.getPassengers()) {
                    notificationService.createNotification(passenger.getId(), flight.getId(), message, type);
                }
            }
        }

        return flightMapper.toResponseDTO(updatedFlight);
    }

    @Override
    @Transactional
    public FlightResponseDTO updateGate(Long id, String gate) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado con id: " + id));

        flight.setGate(gate);
        Flight updatedFlight = flightRepository.save(flight);

        List<Booking> bookings = bookingRepository.findByFlightId(id);
        String message = "El vuelo " + flight.getFlightCode() + " ha actualizado su puerta de embarque a: " + gate;

        for (Booking booking : bookings) {
            if (booking.getUser() != null) {
                notificationService.createUserNotification(booking.getUser().getId(), message, NotificationType.CAMBIO_PUERTA);
            }
            for (com.flytrack.model.Passenger passenger : booking.getPassengers()) {
                notificationService.createNotification(passenger.getId(), flight.getId(), message, NotificationType.CAMBIO_PUERTA);
            }
        }

        return flightMapper.toResponseDTO(updatedFlight);
    }
}
