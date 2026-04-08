package com.flytrack.controller;

import com.flytrack.dto.FlightRequestDTO;
import com.flytrack.dto.FlightResponseDTO;
import com.flytrack.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponseDTO createFlight(@Valid @RequestBody FlightRequestDTO dto) {
        return flightService.createFlight(dto);
    }

    @PutMapping("/{id}")
    public FlightResponseDTO updateFlight(@PathVariable Long id, @Valid @RequestBody FlightRequestDTO dto) {
        return flightService.updateFlight(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }

    @GetMapping("/{id}")
    public FlightResponseDTO getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }

    @GetMapping
    public List<FlightResponseDTO> getAllFlights(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return flightService.getFlightsByStatus(estado);
        }
        return flightService.getAllFlights();
    }

    @GetMapping("/search")
    public List<FlightResponseDTO> searchFlights(@RequestParam String origen, @RequestParam String destino) {
        return flightService.getFlightsByOriginAndDestination(origen, destino);
    }

    @PatchMapping("/{id}/estado")
    public FlightResponseDTO changeStatus(@PathVariable Long id, @RequestParam String estado) {
        return flightService.changeStatus(id, estado);
    }

    @PatchMapping("/{id}/puerta")
    public FlightResponseDTO updateGate(@PathVariable Long id, @RequestParam String puerta) {
        return flightService.updateGate(id, puerta);
    }
}

