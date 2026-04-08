package com.flytrack.controller;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.service.BaggageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/baggage")
@RequiredArgsConstructor
public class BaggageController {

    private final BaggageService baggageService;

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
        return baggageService.getBaggageByTrackingCode(codigoSeguimiento);
    }

    @PatchMapping("/{id}/lost")
    public BaggageResponseDTO reportLost(@PathVariable String id) {
        return baggageService.reportLost(id);
    }
}

