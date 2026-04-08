package com.flytrack.service.impl;

import com.flytrack.dto.PassengerRequestDTO;
import com.flytrack.dto.PassengerResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.PassengerMapper;
import com.flytrack.model.Passenger;
import com.flytrack.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    private Passenger passenger;
    private PassengerRequestDTO requestDTO;
    private PassengerResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        passenger = new Passenger();
        passenger.setId(1L);
        passenger.setFirstName("John");
        passenger.setLastName("Doe");
        passenger.setEmail("john.doe@example.com");

        requestDTO = new PassengerRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setEmail("john.doe@example.com");

        responseDTO = new PassengerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");
        responseDTO.setEmail("john.doe@example.com");
    }

    @Test
    void createPassenger_Success() {
        when(passengerRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passengerMapper.toEntity(requestDTO)).thenReturn(passenger);
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(passengerMapper.toResponseDTO(passenger)).thenReturn(responseDTO);

        PassengerResponseDTO result = passengerService.createPassenger(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(passengerRepository).save(any(Passenger.class));
    }

    @Test
    void createPassenger_ThrowsBusinessException_WhenEmailExists() {
        when(passengerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                passengerService.createPassenger(requestDTO)
        );

        assertTrue(exception.getMessage().contains("ya se encuentra registrado"));
        verify(passengerRepository, never()).save(any());
    }

    @Test
    void getPassengerById_Success() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(passengerMapper.toResponseDTO(passenger)).thenReturn(responseDTO);

        PassengerResponseDTO result = passengerService.getPassengerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPassengerById_ThrowsResourceNotFoundException() {
        when(passengerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                passengerService.getPassengerById(1L)
        );
    }

    @Test
    void getAllPassengers_Success() {
        when(passengerRepository.findAll()).thenReturn(List.of(passenger));
        when(passengerMapper.toResponseDTO(passenger)).thenReturn(responseDTO);

        List<PassengerResponseDTO> result = passengerService.getAllPassengers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}

