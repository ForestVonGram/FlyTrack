package com.flytrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO {
    private Long id;
    private String flightCode;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime estimatedArrivalTime;
    private String status;
    private String gate;
    private String airline;
}

