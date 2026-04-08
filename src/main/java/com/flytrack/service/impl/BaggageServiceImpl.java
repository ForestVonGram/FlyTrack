package com.flytrack.service.impl;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.exception.BusinessException;
import com.flytrack.exception.ResourceNotFoundException;
import com.flytrack.mapper.BaggageMapper;
import com.flytrack.model.Baggage;
import com.flytrack.model.Booking;
import com.flytrack.model.enums.BaggageStatus;
import com.flytrack.repository.BaggageRepository;
import com.flytrack.repository.BookingRepository;
import com.flytrack.service.BaggageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaggageServiceImpl implements BaggageService {

    private final BaggageRepository baggageRepository;
    private final BookingRepository bookingRepository;
    private final BaggageMapper baggageMapper;

    @Override
    @Transactional
    public BaggageResponseDTO registerBaggage(BaggageRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + dto.getBookingId()));

        Baggage baggage = Baggage.builder()
                .booking(booking)
                .trackingCode(UUID.randomUUID().toString())
                .weight(dto.getWeight())
                .status(BaggageStatus.REGISTRADO)
                .build();

        Baggage savedBaggage = baggageRepository.save(baggage);
        return baggageMapper.toResponseDTO(savedBaggage);
    }

    @Override
    @Transactional
    public BaggageResponseDTO updateStatus(String trackingCode, String status) {
        Baggage baggage = baggageRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Equipaje no encontrado con código: " + trackingCode));

        try {
            baggage.setStatus(BaggageStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de equipaje inválido: " + status);
        }

        Baggage updatedBaggage = baggageRepository.save(baggage);
        return baggageMapper.toResponseDTO(updatedBaggage);
    }

    @Override
    @Transactional(readOnly = true)
    public BaggageResponseDTO getBaggageByTrackingCode(String trackingCode) {
        Baggage baggage = baggageRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Equipaje no encontrado con código: " + trackingCode));
        return baggageMapper.toResponseDTO(baggage);
    }

    @Override
    @Transactional
    public BaggageResponseDTO reportLost(String trackingCode) {
        Baggage baggage = baggageRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Equipaje no encontrado con código: " + trackingCode));

        baggage.setStatus(BaggageStatus.PERDIDO);
        Baggage updatedBaggage = baggageRepository.save(baggage);
        return baggageMapper.toResponseDTO(updatedBaggage);
    }
}

