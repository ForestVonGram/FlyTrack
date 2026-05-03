package com.flytrack.service.impl;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;
import com.flytrack.mapper.BaggageMapper;
import com.flytrack.model.Baggage;
import com.flytrack.model.Passenger;
import com.flytrack.model.enums.BaggageStatus;
import com.flytrack.repository.BaggageRepository;
import com.flytrack.repository.PassengerRepository;
import com.flytrack.service.BaggageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaggageServiceImpl implements BaggageService {

    private final BaggageRepository baggageRepository;
    private final PassengerRepository passengerRepository;
    private final BaggageMapper baggageMapper;

    @Override
    @Transactional
    public BaggageResponseDTO registerBaggage(BaggageRequestDTO req) {
        Passenger passenger = passengerRepository.findById(req.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found"));

        Baggage baggage = baggageMapper.toEntity(req);
        baggage.setPassenger(passenger);
        baggage.setTrackingCode(UUID.randomUUID().toString());
        baggage.setStatus(BaggageStatus.REGISTRADO);

        return baggageMapper.toResponseDTO(baggageRepository.save(baggage));
    }

    @Override
    @Transactional
    public BaggageResponseDTO updateStatus(String trackingCode, String status) {
        Baggage baggage = baggageRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
        baggage.setStatus(BaggageStatus.valueOf(status));
        return baggageMapper.toResponseDTO(baggageRepository.save(baggage));
    }

    @Override
    public BaggageResponseDTO getBaggageByTrackingCode(String trackingCode) {
        return baggageRepository.findByTrackingCode(trackingCode)
                .map(baggageMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
    }

    @Override
    @Transactional
    public BaggageResponseDTO reportLost(String trackingCode) {
        Baggage baggage = baggageRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Baggage not found"));
        baggage.setStatus(BaggageStatus.PERDIDO);
        return baggageMapper.toResponseDTO(baggageRepository.save(baggage));
    }
}
