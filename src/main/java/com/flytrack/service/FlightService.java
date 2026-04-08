package com.flytrack.service;

import com.flytrack.dto.FlightRequestDTO;
import com.flytrack.dto.FlightResponseDTO;

import java.util.List;

public interface FlightService {
    FlightResponseDTO createFlight(FlightRequestDTO dto);
    FlightResponseDTO updateFlight(Long id, FlightRequestDTO dto);
    void deleteFlight(Long id);
    FlightResponseDTO getFlightById(Long id);
    List<FlightResponseDTO> getAllFlights();
    List<FlightResponseDTO> getFlightsByStatus(String status);
    List<FlightResponseDTO> getFlightsByOriginAndDestination(String origin, String destination);
    FlightResponseDTO changeStatus(Long id, String status);
    FlightResponseDTO updateGate(Long id, String gate);
}

