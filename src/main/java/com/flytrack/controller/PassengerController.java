package com.flytrack.controller;

import com.flytrack.dto.PassengerRequestDTO;
import com.flytrack.dto.PassengerResponseDTO;
import com.flytrack.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponseDTO createPassenger(@Valid @RequestBody PassengerRequestDTO dto) {
        return passengerService.createPassenger(dto);
    }

    @PutMapping("/{id}")
    public PassengerResponseDTO updatePassenger(@PathVariable Long id, @Valid @RequestBody PassengerRequestDTO dto) {
        return passengerService.updatePassenger(id, dto);
    }

    @GetMapping("/{id}")
    public PassengerResponseDTO getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id);
    }

    @GetMapping
    public List<PassengerResponseDTO> getAllPassengers() {
        return passengerService.getAllPassengers();
    }
}

