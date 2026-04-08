package com.flytrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaggageResponseDTO {
    private Long id;
    private String trackingCode;
    private BigDecimal weight;
    private String status;
    private BookingSummaryDTO booking;
}

