package com.flytrack.controller;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.model.Baggage;
import com.flytrack.model.Booking;
import com.flytrack.model.Passenger;
import com.flytrack.model.User;
import com.flytrack.repository.BaggageRepository;
import com.flytrack.repository.BookingRepository;
import com.flytrack.repository.PassengerRepository;
import com.flytrack.repository.UserRepository;
import com.flytrack.service.BaggageService;
import com.flytrack.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/baggage")
@RequiredArgsConstructor
public class BaggageController {

    private final BaggageService baggageService;
    private final CurrentUserService currentUserService;
    private final BaggageRepository baggageRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaggageResponseDTO registerBaggage(@Valid @RequestBody BaggageRequestDTO dto) {
        return baggageService.registerBaggage(dto);
    }

    @PatchMapping("/{id}/estado")
    public BaggageResponseDTO updateStatus(@PathVariable String id, @RequestParam String status) {
        return baggageService.updateStatus(id, status);
    }

    @GetMapping("/tracking/{codigoSeguimiento}")
    public BaggageResponseDTO getBaggageByTrackingCode(@PathVariable String codigoSeguimiento) {
        // Aplicar trim al código de seguimiento
        String codigoLimpio = codigoSeguimiento.trim();
        log.info("ENTRADA - getBaggageByTrackingCode - Código de seguimiento: {}", codigoLimpio);

        BaggageResponseDTO response = baggageService.getBaggageByTrackingCode(codigoLimpio);
        log.info("SALIDA - getBaggageByTrackingCode - DTO devuelto: {}", response);
        return response;
    }

    @PatchMapping("/{codigoSeguimiento}/lost")
    public BaggageResponseDTO reportLost(@PathVariable String codigoSeguimiento) {
        // Aplicar trim al código de seguimiento
        String codigoLimpio = codigoSeguimiento.trim();
        log.info("ENTRADA - reportLost - Código de seguimiento: {}", codigoLimpio);

        BaggageResponseDTO response = baggageService.reportLost(codigoLimpio);
        log.info("SALIDA - reportLost - DTO devuelto: {}", response);
        return response;
    }
}
