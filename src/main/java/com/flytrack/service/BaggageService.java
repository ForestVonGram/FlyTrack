package com.flytrack.service;

import com.flytrack.dto.BaggageRequestDTO;
import com.flytrack.dto.BaggageResponseDTO;

public interface BaggageService {
    BaggageResponseDTO registerBaggage(BaggageRequestDTO dto);
    BaggageResponseDTO updateStatus(String trackingCode, String status);
    BaggageResponseDTO getBaggageByTrackingCode(String trackingCode);
    BaggageResponseDTO reportLost(String trackingCode);
}

