package com.flytrack.service;

import com.flytrack.dto.PassengerRequestDTO;
import com.flytrack.dto.PassengerResponseDTO;

import java.util.List;

public interface PassengerService {
    PassengerResponseDTO createPassenger(PassengerRequestDTO dto);
    PassengerResponseDTO updatePassenger(Long id, PassengerRequestDTO dto);
    PassengerResponseDTO getPassengerById(Long id);
    PassengerResponseDTO getPassengerByEmail(String email);
    List<PassengerResponseDTO> getAllPassengers();
}

