package com.flytrack.service.impl;

import com.flytrack.dto.PassengerRequestDTO;
import com.flytrack.dto.PassengerResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.PassengerMapper;
import com.flytrack.model.Passenger;
import com.flytrack.repository.PassengerRepository;
import com.flytrack.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    @Transactional
    public PassengerResponseDTO createPassenger(PassengerRequestDTO dto) {
        if (passengerRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("El email ya se encuentra registrado: " + dto.getEmail());
        }

        Passenger passenger = passengerMapper.toEntity(dto);
        Passenger savedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toResponseDTO(savedPassenger);
    }

    @Override
    @Transactional
    public PassengerResponseDTO updatePassenger(Long id, PassengerRequestDTO dto) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado con id: " + id));

        if (!passenger.getEmail().equals(dto.getEmail()) && passengerRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("El email ya se encuentra registrado: " + dto.getEmail());
        }

        passenger.setFirstName(dto.getFirstName());
        passenger.setLastName(dto.getLastName());
        passenger.setEmail(dto.getEmail());
        passenger.setIdentityDocument(dto.getIdentityDocument());
        passenger.setPhoneNumber(dto.getPhoneNumber());

        Passenger updatedPassenger = passengerRepository.save(passenger);
        return passengerMapper.toResponseDTO(updatedPassenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDTO getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado con id: " + id));
        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDTO getPassengerByEmail(String email) {
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Pasajero no encontrado con email: " + email));
        return passengerMapper.toResponseDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassengerResponseDTO> getAllPassengers() {
        return passengerRepository.findAll().stream()
                .map(passengerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}

